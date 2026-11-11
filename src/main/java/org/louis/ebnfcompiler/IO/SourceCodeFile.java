package org.louis.ebnfcompiler.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SourceCodeFile {
    final private ArrayList<String> content;

    public SourceCodeFile(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        File file = new File(path);
        this.content = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                this.content.add(fileScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public ArrayList<String> getContent() {
        return this.content;
    }
}
