package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

abstract public class BinaryExpression extends SyntacticExpression {
    final private SyntacticExpression lhs;
    final private SyntacticExpression rhs;
    final private Token operator;  // DIS_OP or null

    public BinaryExpression(SyntacticExpression lhs, SyntacticExpression rhs, Token operator) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    public SyntacticExpression getLhs() {
        return this.lhs;
    }

    public SyntacticExpression getRhs() {
        return this.rhs;
    }

    public Token getOperator() {
        return this.operator;
    }
}
