package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class DisjunctionExpression extends BinaryExpression implements OpenToVisitors {
    public DisjunctionExpression(SyntacticExpression lhs, SyntacticExpression rhs, Token operator) {
        super(lhs, rhs, operator);
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitDisjunctionExpression(this, args);
    }
}
