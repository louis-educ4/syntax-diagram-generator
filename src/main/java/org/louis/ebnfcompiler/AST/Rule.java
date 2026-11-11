package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class Rule implements OpenToVisitors {
    final private Identifier identifier;
    final private SyntacticExpression expression;

    public Rule(Identifier identifier, SyntacticExpression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitRule(this, args);
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public SyntacticExpression getExpression() {
        return this.expression;
    }
}
