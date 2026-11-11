package org.louis.ebnfcompiler.Diagram;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<List<Node>> edges;
    private Node entryNode;
    private Node outputNode;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node from, Node to) {
        this.edges.add(List.of(from, to));
    }

    public void insert(Graph graph) {
        // Add nodes
        this.nodes.addAll(graph.getNodes());
        this.edges.addAll(graph.getEdges());
    }

    public Node getEntryNode() {
        return this.entryNode;
    }

    public void setEntryNode(Node entryNode) {
        this.entryNode = entryNode;
    }

    public Node getOutputNode() {
        return this.outputNode;
    }

    public void setOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }

    public ArrayList<List<Node>> getEdges() {
        return this.edges;
    }
}
