package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

abstract public class UnaryExpression extends SyntacticExpression {
    final private SyntacticExpression operand;
    final private Token operator;  // PLUS_OP, STAR_OP, null

    public UnaryExpression(SyntacticExpression operand, Token operator) {
        this.operand = operand;
        this.operator = operator;
    }


    public Token getOperator() {
        return this.operator;
    }

    public SyntacticExpression getOperand() {
        return this.operand;
    }
}
