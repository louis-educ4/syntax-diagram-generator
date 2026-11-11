package org.louis.ebnfcompiler.Visitors;

import java.util.*;

public class SymbolTable {
    public static final String PROGRAM_IDENTIFIER = "program";
    private Set<String> referencedSymbols;
    private Map<String, String> scope;

    public SymbolTable() {
        this.referencedSymbols = new HashSet<>();
        this.scope = new HashMap<>();
    }

    /*
    * Define a symbol in the scope
    *
    * @param name
    * @param definition
    */
    public void define(String name, String definition) {
        this.scope.put(name, definition);
    }

    /*
    * Lookup a symbol in the scope
    * return the symbol value
    *
    * @param name
    * @return String: the symbol value or null if not found
    */
    public Object lookup(String name) {
        return this.scope.get(name);
    }

    /*
    * Register a symbol as seen
    *
    * @param name
    */
    public void seen(String name) {
        this.referencedSymbols.add(name);
    }

    public Set<String> getReferencedSymbols() {
        return this.referencedSymbols;
    }

    public Set<String> getDefinedSymbols() {
        return this.scope.keySet();
    }
}
