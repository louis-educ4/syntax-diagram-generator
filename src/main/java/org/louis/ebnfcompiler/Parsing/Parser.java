package org.louis.ebnfcompiler.Parsing;

import org.louis.ebnfcompiler.AST.*;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Lexing.TokenType;

import java.util.ArrayList;
import java.util.EnumSet;

public class Parser {
    final private ArrayList<Token> tokens;
    final private AST ast;
    private int tokenIdx;
    final private int tokenNb;

    private static final EnumSet<TokenType> factorStarter = EnumSet.of(TokenType.L_PAR_DEL, TokenType.L_SQ_BRACE_DEL, TokenType.IDENTIFIER, TokenType.TERMINAL);

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.tokenIdx = 0;
        this.tokenNb = tokens.size();
        this.ast = new AST();
        this.parse();
    }

    private boolean isEndReached() {
        return this.tokenIdx >= this.tokenNb;
    }

    private Token showNext(int index) {
        // Returns the k-th next token, where k is the index.
        // If the index is out of bounds, throws an exception.
        int k = this.tokenIdx + index;
        if (k >= 0 && k < this.tokenNb) {
            return this.tokens.get(k);
        } else {
            throw new IndexOutOfBoundsException("Index " + k + " out of bounds");
        }
    }

    private Token showNext() {
        return this.showNext(0);
    }

    private Token consume() {
        Token token = this.showNext();
        this.tokenIdx++;
        return token;
    }

    private Token expect(TokenType type) {
        // Consumes the next token if it matches the given type.
        // Otherwise, throws an exception.
        Token token = this.consume();
        if (token.getType() != type) {
            throw new IllegalArgumentException("Expected " + type + ", got " + token.getType() + " at " + token.getLine() + ":" + token.getColumn());
        } else {
            return token;
        }
    }

    private Token maybe(TokenType type) {
        Token token = this.showNext();
        if (token.getType() == type) {
            return this.consume();
        } else {
            return null;
        }
    }

    private Identifier parseIdentifier() {
        Token identifier = this.expect(TokenType.IDENTIFIER);
        return new Identifier(identifier);
    }

    private Terminal parseTerminal() {
        Token terminal = this.expect(TokenType.TERMINAL);
        return new Terminal(terminal);
    }

    private SyntacticExpression parseGroupedSequence() {
        this.expect(TokenType.L_PAR_DEL);
        SyntacticExpression expression = this.parseExpression();
        this.expect(TokenType.R_PAR_DEL);
        return expression;
    }

    private SyntacticExpression parseOptionalSequence() {
        this.expect(TokenType.L_SQ_BRACE_DEL);
        SyntacticExpression expression = this.parseExpression();
        this.expect(TokenType.R_SQ_BRACE_DEL);
        return new OptionalExpression(expression);
    }

    private SyntacticExpression parsePrimary() {
        return switch (this.showNext().getType()) {
            case TokenType.L_PAR_DEL -> this.parseGroupedSequence();
            case TokenType.L_SQ_BRACE_DEL -> this.parseOptionalSequence();
            case TokenType.IDENTIFIER -> this.parseIdentifier();
            case TokenType.TERMINAL -> this.parseTerminal();
            default ->
                    throw new IllegalArgumentException("Unexpected token: " + this.showNext() + ". Expected an identifier, a terminal, a grouped sequence, or an optional sequence.");
        };
    }

    private SyntacticExpression parseFactor() {
        SyntacticExpression primary = this.parsePrimary();
        Token op;
        switch (this.showNext().getType()) {
            case TokenType.PLUS_OP:
                op = this.consume();
                return new PlusExpression(primary, op);
            case TokenType.STAR_OP:
                op = this.consume();
                return new StarExpression(primary, op);
            default:
                return primary;
        }
    }

    private SyntacticExpression parseTerm() {
        SyntacticExpression lhs = this.parseFactor();
        while (factorStarter.contains(this.showNext().getType())) {
            SyntacticExpression rhs = this.parseFactor();
            lhs = new ConjunctionExpression(lhs, rhs);
        }
        return lhs;
    }

    private SyntacticExpression parseExpression() {
        SyntacticExpression lhs = this.parseTerm();
        while (this.showNext().getType() == TokenType.DIS_OP) {
            Token op = this.consume();
            SyntacticExpression rhs = this.parseTerm();
            lhs = new DisjunctionExpression(lhs, rhs, op);
        }
        return lhs;
    }

    private Rule parseRule() {
        Identifier lhs = this.parseIdentifier();
        this.expect(TokenType.RULE_OP);
        SyntacticExpression expression = this.parseExpression();
        this.expect(TokenType.NEWLINE);
        return new Rule(lhs, expression);
    }

    private void parse() {
        while (!this.isEndReached()) {
            this.ast.addRule(this.parseRule());
        }
    }

    public AST getAST() {
        return this.ast;
    }
}
