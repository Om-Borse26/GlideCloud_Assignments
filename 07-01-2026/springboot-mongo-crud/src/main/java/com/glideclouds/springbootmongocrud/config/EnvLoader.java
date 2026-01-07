package com.glideclouds.springbootmongocrud.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnvLoader {

    public static void loadEnv() {
        Path envPath = Paths.get(".env");

        if (!Files.exists(envPath)) {
            System.err.println("Warning: .env file not found. Using system environment variables.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse KEY=VALUE format
                int separatorIndex = line.indexOf('=');
                if (separatorIndex > 0) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();

                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    // Set as system property (Spring Boot will pick these up)
                    System.setProperty(key, value);
                }
            }
            System.out.println("Successfully loaded .env file");
        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }
    }
}

