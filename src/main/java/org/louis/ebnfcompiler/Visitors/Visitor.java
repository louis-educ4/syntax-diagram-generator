package org.louis.ebnfcompiler.Visitors;

import org.louis.ebnfcompiler.AST.*;

public class Visitor {
    public Object visitAst(AST ast) {
        for (Rule rule : ast.getRules()) {
            rule.accept(this);
        }
        return null;
    }

    public Object visitRule(Rule rule, Object... args) {
        rule.getIdentifier().accept(this);
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
