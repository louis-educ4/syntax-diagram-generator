package org.louis.ebnfcompiler.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PlaintextOutput {
    private String content = "";
    public PlaintextOutput() {}

    public void setContent(String content) {
        this.content = content;
    }

    public boolean save(String outputPath) {
        File file = new File(outputPath);
        // Check if the file exists, if so return
        if (file.exists() && !file.isDirectory()) {
            System.out.println("[!] " + "%s".formatted(outputPath) + " already exists, can't write to it");
            return false;
        }
        // Try to create the file, return if it fails
        try {
            if (!file.createNewFile()) {
                System.out.println("[!] Error creating output file");
                return false;
            }
        } catch (IOException e) {
            System.out.println("[!] Error creating output file");
            return false;
        }
        // Try to write to the file, return if it fails
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(this.content.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("[i] Saved to " + outputPath);
            return true;
        } catch (IOException e) {
            System.out.println("[!] Error writing to file: " + e.getMessage());
            return false;
        }
    }
}
