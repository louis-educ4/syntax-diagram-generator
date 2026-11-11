package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class PlusExpression extends UnaryExpression implements OpenToVisitors {
    public PlusExpression(SyntacticExpression operand, Token operator) {
        super(operand, operator);
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitPlusExpression(this, args);
    }
}
