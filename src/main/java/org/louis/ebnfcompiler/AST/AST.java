package org.louis.ebnfcompiler.AST;


import java.util.ArrayList;

public class AST {
    private ArrayList<Rule> rules;

    public AST() {
        this.rules = new ArrayList<Rule>();
    }

    public ArrayList<Rule> getRules() {
        return this.rules;
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }
}

