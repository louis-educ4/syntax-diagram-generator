package org.louis.ebnfcompiler.Visitors;

import org.louis.ebnfcompiler.AST.*;
import org.louis.ebnfcompiler.IO.PlaintextOutput;

import java.util.ArrayList;

/*
 * Pretty prints the AST
 * Indents multiple definitions
 * Calculate a minimal number of parentheses.
 * TODO: Makes text colorized, bold, italic, etc.
 */
public class PrettyPrinterVisitor extends Visitor {

    private ArrayList<String> identifiers;
    private ArrayList<String> definitions;
    private int indentLevel;
    private boolean saveOutput;
    private String outputPath;

    /*
    * Constructor
    * Instantiate the visitor
    *
    * All the inner variables are initialized on AST visitation
    */
    public PrettyPrinterVisitor() {
        this.saveOutput = false;
    }

    public PrettyPrinterVisitor(String outputPath) {
        this();
        if (outputPath != null) {
            this.saveOutput = true;
            this.outputPath = outputPath;
        } else {
            this.saveOutput = false;
        }
    }

    /*
    * Visit the AST
    *
    * Iterates over all rules and return the pretty representation of the AST
    *
    * @param ast
    * @return String: the pretty representation of the AST
    */
    @Override
    public Object visitAst(AST ast) {
        this.identifiers = new ArrayList<>();
        this.definitions = new ArrayList<>();
        this.indentLevel = 0;

        for (Rule rule : ast.getRules()) {
            rule.accept(this);
        }

        String repr = "";
        for (int i = 0; i < this.identifiers.size(); i++) {
            String ruleId = this.identifiers.get(i);
            repr += "<" + ruleId + ">" + " ".repeat(this.indentLevel - ruleId.length()) + " ::= " + this.definitions.get(i) + "\n";
        }
        if (this.saveOutput) {
            PlaintextOutput output = new PlaintextOutput();
            output.setContent(repr);
            output.save(this.outputPath);
        }
        return repr;
    }

    /*
    * Visit a rule
    * Retrieve the rule id and definition
    * Update the maximum indent level of the source code
    *
    * @param rule
    * @return null
    */
    @Override
    public Object visitRule(Rule rule, Object... args) {
        String ruleId = rule.getIdentifier().getToken().getValue();
        this.indentLevel = Integer.max(ruleId.length(), this.indentLevel);
        this.identifiers.add(ruleId);
        this.definitions.add(rule.getExpression().accept(this).toString());
        return null;
    }

    /*
    * Visit a conjuction expression
    * Add parentheses around an operand if it is a disjunction expression (only case where we need to force the priority
    * of the inner operation)
    * @see "OperationOrder.txt"
    *
    * @param conjunctionExpression
    * @return String: the pretty representation of the expression
    */
    @Override
    public Object visitConjunctionExpression(ConjunctionExpression conjunctionExpression, Object... args) {
        SyntacticExpression lhs = conjunctionExpression.getLhs();
        SyntacticExpression rhs = conjunctionExpression.getRhs();
        String lhsStr = lhs.accept(this).toString();
        String rhsStr = rhs.accept(this).toString();
        if (lhs instanceof DisjunctionExpression) {
            lhsStr = "(" + lhsStr + ")";
        }
        if (rhs instanceof DisjunctionExpression) {
            rhsStr = "(" + rhsStr + ")";
        }
        return lhsStr + " " + rhsStr;
    }

    /*
    * Visit a disjunction expression
    * Parse both operands and concatenate them with a pipe
    */
    @Override
    public Object visitDisjunctionExpression(DisjunctionExpression disjunctionExpression, Object... args) {
        SyntacticExpression lhs = disjunctionExpression.getLhs();
        SyntacticExpression rhs = disjunctionExpression.getRhs();
        return lhs.accept(this).toString() + " | " + rhs.accept(this).toString();
    }

    /*
    * Visit a star expression
    * If the operand is a conjunction or disjunction expression, wrap it in parentheses (cases where we need to force
    * the priority of the inner operation)
    *
    * @see "OperationOrder.txt"
    *
    * @param starExpression
    * @return String: the pretty representation of the expression
    */
    @Override
    public Object visitStarExpression(StarExpression starExpression, Object... args) {
        SyntacticExpression operand = starExpression.getOperand();
        if (operand instanceof ConjunctionExpression || operand instanceof DisjunctionExpression) {
            return "( " + operand.accept(this).toString() + " )*";
        } else {
            return operand.accept(this).toString() + "*";
        }
    }

    /*
    * Visit a plus expression
    * If the operand is a conjunction or disjunction expression, wrap it in parentheses (cases where we need to force
    * the priority of the inner operation)
    *
    * @see "OperationOrder.txt"
    *
    * @param plusExpression
    * @return String: the pretty representation of the expression
    */
    @Override
    public Object visitPlusExpression(PlusExpression plusExpression, Object... args) {
        SyntacticExpression operand = plusExpression.getOperand();
        if (operand instanceof ConjunctionExpression || operand instanceof DisjunctionExpression) {
            return "( " + operand.accept(this).toString() + " )+";
        } else {
            return operand.accept(this).toString() + "+";
        }
    }

    /*
    * Visit an optional expression
    * Wrap the operand in square brackets
    *
    * @param optionalExpression
    * @return String: the pretty representation of the expression
    */
    @Override
    public Object visitOptionalExpression(OptionalExpression optionalExpression, Object... args) {
        return "[ " + optionalExpression.getOperand().accept(this) + " ]";
    }

    /*
    * Visit an identifier
    * return the identifier value
    *
    * @param identifier
    * @return String: the identifier value
    */
    @Override
    public Object visitIdentifier(Identifier identifier, Object... args) {
        return "<" + identifier.getToken().getValue() + ">";
    }

    /*
    * Visit a terminal
    * return the terminal value
    *
    * @param terminal
    * @return String: the terminal value
    */
    @Override
    public Object visitTerminal(Terminal terminal, Object... args) {
        return terminal.getToken().getValue();
    }
}
