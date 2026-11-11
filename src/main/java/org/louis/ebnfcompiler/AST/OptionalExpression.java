package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class OptionalExpression extends UnaryExpression implements OpenToVisitors {
    public OptionalExpression(SyntacticExpression operand) {
        super(operand, null);
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitOptionalExpression(this, args);
    }
}
