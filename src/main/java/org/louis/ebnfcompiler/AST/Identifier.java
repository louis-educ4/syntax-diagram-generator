package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class Identifier extends SyntacticExpression implements OpenToVisitors {
    final private Token identifier; // Identifier

    public Identifier(Token identifier) {
        this.identifier = identifier;
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitIdentifier(this, args);
    }

    public Token getToken() {
        return this.identifier;
    }
}
