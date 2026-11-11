package org.louis.ebnfcompiler.Lexing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    final private ArrayList<Token> tokens;

    public Lexer(ArrayList<String> sourceContent) {
        this.tokens = new ArrayList<>();

        boolean anyTokenFound = false;

        // Lex the file
        for (int lineIdx=0; lineIdx<sourceContent.size(); lineIdx++) {
            int columnIdx = 0;
            String line = sourceContent.get(lineIdx);
            while (columnIdx < line.length()) {
                for (TokenType tokenType : TokenType.values()) {
                    Pattern pattern = Pattern.compile(tokenType.getRegex());
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find(columnIdx)) {
                        if (matcher.start() == columnIdx) {
                            String value = matcher.group(0);
                            this.tokens.add(new Token(tokenType, removeBrackets(value), lineIdx, columnIdx));
                            columnIdx += value.length();
                            anyTokenFound = true;
                            break;
                        }
                    }
                }
                // If no token was found, move to the next column
                if (anyTokenFound) {
                    anyTokenFound = false;
                } else {
                    columnIdx++;
                }
            }
        }

        // Remove whitespace tokens
        this.tokens.removeIf(token -> token.getType() == TokenType.WHITESPACE);
    }

    public String removeBrackets(String value) {
        if (value.startsWith("<")) {
            value = value.substring(1, value.length());
        }
        if (value.endsWith(">")) {
            return value.substring(0, value.length()-1);
        }
        return value;
    }

    public ArrayList<Token> getTokens() {
        return this.tokens;
    }
}
