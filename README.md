# Grammar diagram generator

Mon projet de compilateur permet de

- Lexer un fichier dans un langage proche de l'EBNF

- Parser les léxèmes issus du lexing

- Afficher ces léxèmes

- Construire un arbre de syntaxe abstraite

- Faire des tests d'analyse contextuelle

- Formatter le fichier

- Enregistrer ce formattage

- Générer un le code d'un diagramme Graphviz/DOT, l'afficher, et l'enregistrer.

## Language d'entrée : une variante d'EBNF

Nous utilisons la syntaxe EBNF suivante :

| Nom                    | Syntaxe                | Signification                                                          |
| ---------------------- | ---------------------- | ---------------------------------------------------------------------- |
| Identifiant            | <[a-zA-Z\_]+>          | Désigne un symbole non terminal.                                       |
| Définition d'une règle | <...> ::= ... ;        | Définit un symbole non terminal.                                       |
| Disjonction            | $a \char"007C b$       | Indique la présence de $a$ ou de $b$.                                  |
| Conjonction            | $a\,\, b$              | Indique la présence de $a$ puis de $b$.                                |
| Groupe                 | $(\dots)$              | Permet de grouper des opérations EBNF (Disjonction, conjonction, ...). |
| Optionnalité           | $[a]$                  | La présence de $a$ est optionnelle.                                    |
| 0+                     | $a*$                   | $a$ apparaît $0$ fois ou plus.                                         |
| 1+                     | $a+$                   | $a$ apparaît $1$ fois ou plus.                                         |
| Terminal               | $"regex"$ ou $'regex'$ | Désigne un symbole terminal par son expression régulière.              |

PS : Nous utiliserons une syntaxe d'expressions régulières Java pour les symboles terminaux.

### Lexèmes

On en déduit les tokens suivants :

| Nom            | Regex                                                                                 | Signification                                                |
| -------------- | ------------------------------------------------------------------------------------- | ------------------------------------------------------------ |
| NEWLINE        | $\texttt{;}$                                                                          | Fin de règle                                                 |
| (WHITESPACE)   | $\texttt{[ \textbackslash t\textbackslash r\textbackslash n]+}$                       | Espace, **supprimé après le lexing**                         |
| RULE_OP        | $\texttt{::=}$                                                                        | Définition d'une règle                                       |
| IDENTIFIER     | $\texttt{<[a-zA-Z\_]+>}$                                                              | Identifiant EBNF                                             |
| L_PAR_DEL      | $\texttt{\textbackslash (}$                                                           | Parenthèse gauche                                            |
| R_PAR_DEL      | $\texttt{\textbackslash )}$                                                           | Parenthèse droite                                            |
| L_SQ_BRACE_DEL | $\texttt{\textbackslash [}$                                                           | Crochet gauche, ouvre un contenu optionnel                   |
| R_SQ_BRACE_DEL | $\texttt{\textbackslash ]}$                                                           | Crochet droit, ferme un contenu optionnel                    |
| STAR_OP        | $\texttt{\textbackslash*}$                                                            | Étoile de Kleene                                             |
| PLUS_OP        | $\texttt{\textbackslash+}$                                                            | Plus, contenu présent au moins une fois                      |
| DIS_OP         | $\texttt{\textbackslash \char"007C}$                                                  | Barre oblique, disjonction                                   |
| TERMINAL       | $\texttt{("\textbackslash [\char`\^"\textbackslash ]*") \char"007C ('[\char`\^']*')}$ | Symbole terminal, token désigné par son expression régulière |

### Syntaxe concrète

Voici la grammaire de l'EBNF considérée, exprimée elle même en EBNF. Les tokens sont en majuscules.

```plaintext
<grammar>           ::= <rule>* ;
<rule>              ::= <IDENTIFIER> <RULE_OP> <expression> <NEWLINE> ;
<expression>        ::= <term> (<DIS_OP> <term>)* ;
<term>              ::= <factor>+ ;
<factor>            ::= <primary> [ <STAR_OP> | <PLUS_OP> ] ;
<primary>           ::= <optional_sequence>
                     | <grouped_sequence>
                     | <IDENTIFIER>
                     | <TERMINAL>;
<optional_sequence> ::= <L_SQ_BRACE_DEL> <expression> <R_SQ_BRACE_DEL> ;
<grouped_sequence>  ::= <L_PAR_DEL> <expression> <R_PAR_DEL> ;

<IDENTIFIER>        ::= "\\<[a-zA-Z_][a-zA-Z0-9_]*\\>";
<RULE_OP>           ::= "::=" ;
<DIS_OP>            ::= "|" ;
<STAR_OP>           ::= "*" ;
<PLUS_OP>           ::= "+" ;
<L_SQ_BRACE_DEL>    ::= "[" ;
<R_SQ_BRACE_DEL>    ::= "]" ;
<L_PAR_DEL>         ::= "(" ;
<R_PAR_DEL>         ::= ")" ;
<NEWLINE>           ::= "\\n" ;
```

## Analyse contextuelle

Le programme propose une vérification par analyse contextuelle.

Dans l'idée, il faut que tous les non-terminaux soit définis et que ces définitions se ramenent à des terminaux. Par exemple,

```ebnf
<a> ::= <b>
<b> ::= <a>
```

n'est pas valide, alors que

```ebnf
<a> ::= <b> | "term"
<b> ::= <a>
```

est valide.

L'ordre des définitions ne doit pas importer pour que de telles défiitions récursives soient correctes. L'utilisation d'un identifiant qui n'est pas encore défini lors d'un parcours de l'AST ne sera gênante que si l'identifiant n'est défini nul part. On interdit la redéfinition d'un identifiant.

On gèrera cette table de hachage avec un objet `SymbolTable` pour séparer la gestion des symboles de la logique d'un visiteur.

## Language de sortie : DOT

Le compilateur peut générer le code d'un diagramme Graphviz. Pour cela, en parcourant l'AST in construit pour chaque règle un graphe, où les noeuds sont les noeuds du diagramme, des noeuds de jonction (à valeur de disjonction), et les arêtes des juxtapositions d'éléments possibles. Le feuilles de l'AST sont toujours des identifiants ou des terminaux.

- On commence le parcours par eux, en créant un graphe avec un seule noeud, et on retient une entrée et une sortie du graphe, ici l'unique noeud. 

- En remontant, si on tombe sur une conjonction, on joint les deux graphes en connectant le noeud de sortie du *lhs* au noeud d'entrée du *rhs*

- Pour une disjonction, on crée un noeud d'entrée qui est relié aux noeuds d'entrée du *lhs* et du *rhs*, et on crée un noeud de sortie, qui peut être atteint par la sortie du *lhs* et celle du *rhs*. Ainsi on garde toujours un unique noeud d'entrée, un unique noeud de sortie et on peut toujours agrandir l'arbre récursivement. Un futur point d'amélioration est de supprimé les noeuds intermédiaires (routing nodes sur le diagramme)

- Pour une optionnalité, on rajoute un noeud d'entrée, et un noeud de sortie, avec une arête qui passe directement de l'un à l'autre, mais aussi un chemin qui passe par le sous-graphe.

- etc.

## Test

Une grammaire de test est dans le dossier `grammar`. Les sorties du programme ainsi que les diagrammes compilés sont dans `examples`.

## Utilisation

Exemple de commande :

```bash
java -jar ebnfcompiler.jar grammar/myEBNF.ebnf --dump-lexing --run-semantic-analysis --pretty-print --export-pretty-print-to myEBNF.pretty_printed.txt --print-graphviz --export-graphviz-to myEBNF.dot
```

end then

```bash
dot -Tpng myEBNF.dot -o myEBNF.dot.png
```
