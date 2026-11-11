package org.louis.ebnfcompiler;

import org.louis.ebnfcompiler.AST.AST;
import org.louis.ebnfcompiler.IO.SourceCodeFile;
import org.louis.ebnfcompiler.Lexing.Lexer;
import org.louis.ebnfcompiler.Lexing.Token;
import org.louis.ebnfcompiler.Parsing.Parser;
import org.louis.ebnfcompiler.Visitors.*;

public class Compiler {
    String sourceCodeFilename;

    public Compiler(String sourceCodeFilename) {
        this.sourceCodeFilename = sourceCodeFilename;
    }

    public void compile(boolean dumpLexing, boolean runSemanticAnalysis, boolean prettyPrint, String prettyPrintOutputFilename, boolean printGraphviz, String graphvizOutputFilename) {
        // Read source file
        SourceCodeFile source = new SourceCodeFile(sourceCodeFilename);

        // Lex source file
        Lexer lexer = new Lexer(source.getContent());
        if (dumpLexing) {
            System.out.println("-- Dumping lexing --");
            for (Token token : lexer.getTokens()) {
                System.out.println(token);
            }
            System.out.println("-------------------\n");
        }

        // Parse source file tokens
        Parser parser = new Parser(lexer.getTokens());
        AST ast = parser.getAST();

        // Test visitor
        if (false) {
            Visitor emptyVisitor = new Visitor();
            emptyVisitor.visitAst(ast);
        }

        // Semantic analyzer
        if (runSemanticAnalysis) {
            System.out.println("-- Checking semantic rules --");
            Visitor semanticAnalyzerVisitor = new SemanticAnalyzerVisitor();
            semanticAnalyzerVisitor.visitAst(ast);
            System.out.println("-----------------------------\n");
        }

        // Pretty printer
        if (prettyPrint | prettyPrintOutputFilename != null) {
            Visitor prettyPrinterVisitor;
            // Initialize the pretty printer visitor, with the output filename if specified
            if (prettyPrintOutputFilename != null) {
                prettyPrinterVisitor = new PrettyPrinterVisitor(prettyPrintOutputFilename);
            } else {
                prettyPrinterVisitor = new PrettyPrinterVisitor();
            }
            // Run the visitor
            String printed = prettyPrinterVisitor.visitAst(ast).toString();
            // Print the pretty printed code if the user wants to
            if (prettyPrint) {
                System.out.println("-- Pretty printing --");
                System.out.print(printed);
                System.out.println("---------------------\n");
            }
        }

        // Diagram generator
        if (printGraphviz | graphvizOutputFilename != null) {
            Visitor syntaxDiagramGeneratorVisitor;
            // Initialize the syntax diagram generator visitor, with the output filename if specified
            if (graphvizOutputFilename != null) {
                syntaxDiagramGeneratorVisitor = new GraphvizSyntaxDiagramGeneratorVisitor(graphvizOutputFilename);
            } else {
                syntaxDiagramGeneratorVisitor = new GraphvizSyntaxDiagramGeneratorVisitor();
            }
            // Run the visitor
            String graphvizCode = syntaxDiagramGeneratorVisitor.visitAst(ast).toString();

            // Print the graphviz code if the user wants to
            if (printGraphviz) {
                System.out.println("-- Generating syntax diagram --");
                System.out.println(graphvizCode);
                System.out.println("-------------------------------");
            }
            // Print hint to compile the diagram with Graphviz
            if (graphvizOutputFilename != null) {
                System.out.println("[i] To compile the diagram, run the following command:");
                System.out.println("[i] dot -Tpng " + graphvizOutputFilename + " -o " + graphvizOutputFilename + ".png");
            }
        }
    }
}
