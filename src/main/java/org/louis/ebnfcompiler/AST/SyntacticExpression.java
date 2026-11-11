package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public abstract class SyntacticExpression implements OpenToVisitors {
    public abstract Object accept(Visitor visitor, Object... args);
}
