package org.louis.ebnfcompiler.Visitors;

import org.louis.ebnfcompiler.AST.*;
import org.louis.ebnfcompiler.Diagram.Graph;
import org.louis.ebnfcompiler.Diagram.Node;
import org.louis.ebnfcompiler.IO.GraphvizOutput;

import java.util.ArrayList;

public class GraphvizSyntaxDiagramGeneratorVisitor extends Visitor {
    private GraphvizOutput output;
    private String outputPath;
    private boolean saveOutput;

    public GraphvizSyntaxDiagramGeneratorVisitor() {
        this.output = new GraphvizOutput();
    }

    public GraphvizSyntaxDiagramGeneratorVisitor(String outputPath) {
        this();
        if (outputPath != null) {
            this.saveOutput = true;
            this.outputPath = outputPath;
        } else {
            this.saveOutput = false;
        }
    }

    public Object visitAst(AST ast) {
        // Build rule graphs
        ArrayList<Graph> ruleGraphs = new ArrayList<>();
        for (Rule rule : ast.getRules().reversed()) {
            ruleGraphs.add((Graph) rule.accept(this));
        }
        // Remove the intermediary hidden nodes
        // Clean node names
        this.cleanNodeNames(ruleGraphs);
        // Generate graphviz code
        String graphvizCode = this.output.setContent(ruleGraphs);
        // Save them
        if (this.saveOutput) {
            this.output.save(this.outputPath);
        }
        return graphvizCode;
    }

    public void cleanNodeNames(ArrayList<Graph> graphs) {
        ArrayList<String> names = new ArrayList<>();
        int counter;
        for (Graph graph: graphs) {
            for (Node node : graph.getNodes()) {
                String initialName = node.getName();
                counter = 0;
                while (names.contains(node.getName())) {
                    node.setName(initialName + counter);
                    counter++;
                }
                names.add(node.getName());
            }
        }
    }

    public Object visitRule(Rule rule, Object... args) {
        // Retrieve graphs
        Graph idGraph = (Graph) rule.getIdentifier().accept(this);
        Graph ruleGraph = (Graph) rule.getExpression().accept(this);
        // Set Rule ID node as such
        idGraph.getEntryNode().setIsRuleId(true);
        // Create a joint graph
        Graph graph = new Graph();
        graph.insert(idGraph);
        graph.insert(ruleGraph);
        graph.addEdge(idGraph.getOutputNode(), ruleGraph.getEntryNode());
        graph.setEntryNode(idGraph.getEntryNode());
        graph.setOutputNode(ruleGraph.getOutputNode());
        // Return
        return graph;
    }

    public Object visitStarExpression(StarExpression starExpression, Object... args) {
        // Retrieve the graph of the operand
        Graph opGraph = (Graph) starExpression.getOperand().accept(this);
        // Create a new graph containing the operand and the star operator
        Graph graph = new Graph();
        graph.insert(opGraph);
        // Additional nodes, edges and entry/output nodes
        Node entryNode = new Node(false, false, true, "starInput", "");
        Node outputNode = new Node(false, false, true, "starOutput", "");
        graph.addNode(entryNode);
        graph.addNode(outputNode);
        graph.addEdge(entryNode, opGraph.getEntryNode());
        graph.addEdge(opGraph.getOutputNode(), outputNode);
        graph.addEdge(outputNode, entryNode);
        graph.addEdge(entryNode, outputNode);
        graph.setEntryNode(entryNode);
        graph.setOutputNode(outputNode);
        // Return
        return graph;
    }

    public Object visitPlusExpression(PlusExpression plusExpression, Object... args) {
        // Retrieve the graph of the operand
        Graph opGraph = (Graph) plusExpression.getOperand().accept(this);
        // Create a new graph containing the operand and the plus operator
        Graph graph = new Graph();
        graph.insert(opGraph);
        // Additional nodes, edges and entry/output nodes
        Node entryNode = new Node(false, false, true, "plusInput", "");
        Node outputNode = new Node(false, false, true, "plusOutput", "");
        graph.addNode(entryNode);
        graph.addNode(outputNode);
        graph.addEdge(entryNode, opGraph.getEntryNode());
        graph.addEdge(opGraph.getOutputNode(), outputNode);
        graph.addEdge(outputNode, entryNode);
        graph.setEntryNode(entryNode);
        graph.setOutputNode(outputNode);
        // Return
        return graph;
    }

    public Object visitOptionalExpression(OptionalExpression optionalExpression, Object... args) {
        // Retrieve the graph of the operand
        Graph opGraph = (Graph) optionalExpression.getOperand().accept(this);
        // Create a new graph containing the operand and the optional operator
        Graph graph = new Graph();
        graph.insert(opGraph);
        // Additional nodes, edges and entry/output nodes
        Node entryNode = new Node(false, false, true, "optInput", "");
        Node outputNode = new Node(false, false, true, "optOutput", "");
        graph.addNode(entryNode);
        graph.addNode(outputNode);
        graph.addEdge(entryNode, opGraph.getEntryNode());
        graph.addEdge(opGraph.getOutputNode(), outputNode);
        graph.addEdge(entryNode, outputNode);
        graph.setEntryNode(entryNode);
        graph.setOutputNode(outputNode);
        // Return
        return graph;
    }

    public Object visitIdentifier(Identifier identifier, Object... args) {
        Graph graph = new Graph();
        Node idNode = new Node(false, false, false, identifier.getToken().getValue(),
                identifier.getToken().getValue());
        graph.addNode(idNode);
        graph.setEntryNode(idNode);
        graph.setOutputNode(idNode);
        return graph;
    }

    public Object visitTerminal(Terminal terminal, Object... args) {
        Graph graph = new Graph();
        Node terminalNode = new Node(false,
                true,
                false,
                "terminal",
                terminal.getToken().getValue().replaceAll("\"", "“")
        );
        graph.addNode(terminalNode);
        graph.setEntryNode(terminalNode);
        graph.setOutputNode(terminalNode);
        return graph;
    }

    public Object visitConjunctionExpression(ConjunctionExpression conjunctionExpression, Object... args) {
        Graph lGraph = (Graph) conjunctionExpression.getLhs().accept(this);
        Graph rGraph = (Graph) conjunctionExpression.getRhs().accept(this);
        // Create a new graph containing both branches
        Graph conGraph = new Graph();
        conGraph.insert(lGraph);
        conGraph.insert(rGraph);
        // Join the two graphs
        Node lEntryNodeGraph = lGraph.getEntryNode();
        Node rEntryNodeGraph = rGraph.getEntryNode();
        Node lOutputNodeGraph = lGraph.getOutputNode();
        Node rOutputNodeGraph = rGraph.getOutputNode();
        conGraph.addEdge(lOutputNodeGraph, rEntryNodeGraph);
        conGraph.setEntryNode(lEntryNodeGraph);
        conGraph.setOutputNode(rOutputNodeGraph);
        // Return
        return conGraph;
    }

    public Object visitDisjunctionExpression(DisjunctionExpression disjunctionExpression, Object... args) {
        Graph lGraph = (Graph) disjunctionExpression.getLhs().accept(this);
        Graph rGraph = (Graph) disjunctionExpression.getRhs().accept(this);
        // Create a new graph containing both branches
        Graph disjunctionGraph = new Graph();
        disjunctionGraph.insert(lGraph);
        disjunctionGraph.insert(rGraph);
        // Joint entry node
        Node lEntryNodeGraph = lGraph.getEntryNode();
        Node rEntryNodeGraph = rGraph.getEntryNode();
        Node disjunctionEntryNodeGraph = new Node(false, false, true, "disInput", "");
        disjunctionGraph.addNode(disjunctionEntryNodeGraph);
        disjunctionGraph.addEdge(disjunctionEntryNodeGraph, lEntryNodeGraph);
        disjunctionGraph.addEdge(disjunctionEntryNodeGraph, rEntryNodeGraph);
        disjunctionGraph.setEntryNode(disjunctionEntryNodeGraph);
        // Joint output node
        Node lOutputNodeGraph = lGraph.getOutputNode();
        Node rOutputNodeGraph = rGraph.getOutputNode();
        Node disjunctionOutputNodeGraph = new Node(false, false, true, "disOutput", "");
        disjunctionGraph.addNode(disjunctionOutputNodeGraph);
        disjunctionGraph.addEdge(lOutputNodeGraph, disjunctionOutputNodeGraph);
        disjunctionGraph.addEdge(rOutputNodeGraph, disjunctionOutputNodeGraph);
        disjunctionGraph.setOutputNode(disjunctionOutputNodeGraph);
        // Return
        return disjunctionGraph;
    }
}
