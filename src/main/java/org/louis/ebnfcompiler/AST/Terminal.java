package org.louis.ebnfcompiler.AST;

import org.louis.ebnfcompiler.Visitors.OpenToVisitors;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Visitors.Visitor;

import java.util.Optional;

public class Terminal extends SyntacticExpression implements OpenToVisitors {
    final private Token terminal;  // Terminal

    public Terminal(Token terminal) {
        this.terminal = terminal;
    }

    @Override
    public Object accept(Visitor visitor, Object... args) {
        return visitor.visitTerminal(this, args);
    }

    public Token getToken() {
        return this.terminal;
    }
}
