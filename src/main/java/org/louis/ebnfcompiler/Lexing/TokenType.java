package org.louis.ebnfcompiler.Lexing;

/*
* Define the types of tokens in EBNF
*/

public enum TokenType {
    NEWLINE(";"),
    WHITESPACE("[ \t\r\n]+"),
    RULE_OP("::="),  // :=
    IDENTIFIER("<[a-zA-Z_]+>"), // <[a-z]+>
    L_PAR_DEL("\\("), // (
    R_PAR_DEL("\\)"), // )
    L_SQ_BRACE_DEL("\\["), // [
    R_SQ_BRACE_DEL("\\]"), // ]
//    DOUBLE_QUOTE_DEL("\""), // "
//    OPT_OP("\\?"), // ?
    STAR_OP("\\*"), // *
    PLUS_OP("\\+"), // +
    DIS_OP("\\|"), // |
    TERMINAL("(\"[^\"]*\")|('[^']*')") // a-zA-Z0-9, is a token type of the target language. See TargetTokenType.
    ;

    private final String regex;

    TokenType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return this.regex;
    }
}
