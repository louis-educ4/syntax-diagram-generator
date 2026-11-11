package org.louis.ebnfcompiler.Visitors;

import org.louis.ebnfcompiler.AST.*;

import java.util.Set;

import static org.louis.ebnfcompiler.Visitors.SymbolTable.PROGRAM_IDENTIFIER;

/*
 * Checks the AST for semantic errors :
 * Undefined identifiers
 * Redefined identifiers
 */
public class SemanticAnalyzerVisitor extends Visitor {
    private SymbolTable symbolTable;

    public Object visitAst(AST ast) {
        this.symbolTable = new SymbolTable();

        // Save defined identifiers, check for redefinitions, and saved references
        for (Rule rule : ast.getRules()) {
            rule.accept(this);
        }

        // Check for undefined identifiers
        Set<String> referencedSymbols = this.symbolTable.getReferencedSymbols();
        Set<String> definedSymbols = this.symbolTable.getDefinedSymbols();
        for (String symbol : referencedSymbols) {
            if (!definedSymbols.contains(symbol)) {
                System.out.println("Undefined identifier " + symbol);
            }
        }

        // Check for unused identifiers
        for (String symbol : definedSymbols) {
            if (symbol.equals(PROGRAM_IDENTIFIER)){
                break;
            };
            if (!referencedSymbols.contains(symbol)) {
                System.out.println("Unused identifier " + symbol);
            }
        }
        System.out.println("Semantic analysis complete");
        return null;
    }

    public Object visitRule(Rule rule, Object... args) {
        String id = rule.getIdentifier().getToken().getValue();
        if (this.symbolTable.lookup(id) == null) {
            // New symbol defined
            this.symbolTable.define(id, "");
        } else {
            // Symbol already defined
            System.out.println("Identifier " + id + " defined more than once.");
        }
        rule.getExpression().accept(this);
        return null;
    }

    public Object visitStarExpression(StarExpression starExpression, Object... args) {
        starExpression.getOperand().accept(this);
        return null;
    }

    public Object visitPlusExpression(PlusExpression plusExpression, Object... args) {
        plusExpression.getOperand().accept(this);
        return null;
    }

    public Object visitOptionalExpression(OptionalExpression optionalExpression, Object... args) {
        optionalExpression.getOperand().accept(this);
        return null;
    }

    public Object visitIdentifier(Identifier identifier, Object... args) {
        this.symbolTable.seen(identifier.getToken().getValue());
        return null;
    }

    public Object visitTerminal(Terminal terminal, Object... args) {
        return null;
    }

    public Object visitConjunctionExpression(ConjunctionExpression conjunctionExpression, Object... args) {
        conjunctionExpression.getLhs().accept(this);
        conjunctionExpression.getRhs().accept(this);
        return null;
    }

    public Object visitDisjunctionExpression(DisjunctionExpression disjunctionExpression, Object... args) {
        disjunctionExpression.getLhs().accept(this);
        disjunctionExpression.getRhs().accept(this);
        return null;
    }
}
