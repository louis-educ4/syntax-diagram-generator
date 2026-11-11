package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class ConjunctionExpression extends BinaryExpression implements OpenToVisitors {
    public ConjunctionExpression(SyntacticExpression lhs, SyntacticExpression rhs) {
        super(lhs, rhs, null);
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitConjunctionExpression(this, args);
    }
}
