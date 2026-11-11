package org.louis.ebnfcompiler.Lexing;

public class Token {
    final private TokenType type;
    final private String value;
    final private int line;
    final private int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
        return this.type + " " + this.value + " " + this.line + ":" + this.column;
    }
}
