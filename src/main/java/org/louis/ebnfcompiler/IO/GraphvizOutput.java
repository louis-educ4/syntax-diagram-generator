package org.louis.ebnfcompiler.IO;

import org.louis.ebnfcompiler.Diagram.Graph;
import org.louis.ebnfcompiler.Diagram.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GraphvizOutput {
    private String content = "";
    public GraphvizOutput() {}

    private String getRuleGraphviz(Graph ruleGraph) {
        String ruleGraphviz = "";
        for (Node node: ruleGraph.getNodes()) {
            if (node.getIsRuleId()) {
                ruleGraphviz += "    " + node.getName() + " [shape=plaintext, style=\"\", label=\"" + node.getTextContent() + "\"];\n";
            } else if (node.isHidden()) {
                ruleGraphviz += "    " + node.getName() + " [shape=circle, label=\"\", style=\"filled\", fillcolor=\"#808080\"];\n";
            } else if (node.isTerminal()) {
                ruleGraphviz += "    " + node.getName() + " [shape=box, style=\"rounded,filled\", fillcolor=\"#9fc4fc\", label=\"" + node.getTextContent() +  "\"];\n";
            } else {
                ruleGraphviz += "    " + node.getName() + " [shape=box, style=\"filled\", fillcolor=\"#9ffcab\", label=\"" + node.getTextContent() + "\"];\n";
            }
        }
        for (List<Node> edge : ruleGraph.getEdges()) {
            ruleGraphviz += "    " + edge.get(0).getName() + "->" + edge.get(1).getName() + ";\n";
        }
        return ruleGraphviz;
    }

    public String setContent(ArrayList<Graph> rules) {
        this.content = """
                digraph EBNF {
                    rankdir=LR;
                    splines=line;
                    """;
        String graphName;
        for (Graph ruleGraph : rules) {
            graphName =ruleGraph.getEntryNode().getTextContent();
            this.content += "\n    // =====================\n";
            this.content += "    // " + graphName + "\n";
            this.content+= getRuleGraphviz(ruleGraph);
        }
        this.content += "\n    // =====================\n";
        this.content += "    // " + "Legend" + "\n";
        this.content += """
                    subgraph cluster_legend {
                        style="filled";
                        fillcolor="white";
                        color="black";
                        margin=20;
                    
                        legend_title [shape=plaintext, label=<<U>Legend</U>>, fontsize=12];
                    
                        TerminalDefinition [shape=box, style="rounded,filled", fillcolor="#9fc4fc", label="Terminal"];
                        NonTerminalDefinition [shape=box, style="filled", fillcolor="#9ffcab", label="Non-Terminal"];
                        RoutingNode [shape=circle, label="Routing node", style="filled", fillcolor="#808080"];
                    
                        {rank=same; TerminalDefinition; NonTerminalDefinition; RoutingNode;}
                        legend_title -> TerminalDefinition [style=invis];
                    }
                """;
        this.content += "}\n";
        return this.content;
    }

    public boolean save(String outputPath) {
        File file = new File(outputPath);
        // Check if the file exists, if so return
        if (file.exists() && !file.isDirectory()) {
            System.out.println("[!] " + "%s".formatted(outputPath) + " already exists, can't write diagram to it");
            return false;
        }
        // Try to create the file, return if it fails
        try {
            if (!file.createNewFile()) {
                System.out.println("[!] Error creating output diagram file");
                return false;
            }
        } catch (IOException e) {
            System.out.println("[!] Error creating output diagram file");
            return false;
        }
        // Try to write to the file, return if it fails
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(this.content.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("[i] Output diagram saved to " + outputPath);
            return true;
        } catch (IOException e) {
            System.out.println("[!] Error writing to output diagram file: " + e.getMessage());
            return false;
        }
    }
}

