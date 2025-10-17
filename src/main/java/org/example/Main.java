package org.example;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        label:
        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            try {
                switch (command) {
                    case "1":
                        doMaxWordsInFile();
                        break;
                    case "2":
                        doEncryptFile();
                        break;
                    case "3":
                        doDecryptFile();
                        break;
                    case "4":
                        doTagCount();
                        break;
                    case "5":
                        doFilterWriterDemo();
                        break;
                    case "6":
                        doDeserializeObject();
                        break;
                    case "0":
                        break label;
                    default:
                        System.out.println("Unknown option");
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nChoose:");
        System.out.println("1) Find line with max words in file");
        System.out.println("2) Encrypt file (byte-stream)");
        System.out.println("3) Decrypt file (byte-stream)");
        System.out.println("4) Count HTML tags by URL and print sorted results");
        System.out.println("5) Demo FilterWriter/Reader encryption");
        System.out.println("6) Deserialize object from file");
        System.out.println("0) Exit");
        System.out.print("> ");
    }

    private static void doMaxWordsInFile() throws Exception {
        System.out.print("Enter input file path: ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) throw new IllegalArgumentException("File not found: " + inputFile.getAbsolutePath());

        List<String> fileLines = FileManager.readLines(inputFile);
        MaxWordsLine result = TextProcessor.findLineWithMaxWords(fileLines);
        System.out.println("Result: " + result);

        System.out.print("Enter output path for serialization: ");
        String serializationPath = scanner.nextLine().trim();
        File serializationFile = new File(serializationPath);
        FileManager.serializeObject(serializationFile, result);
        System.out.println("Serialized result to: " + serializationFile.getAbsolutePath());
    }

    private static void doEncryptFile() throws Exception {
        System.out.print("Input file to encrypt: ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            throw new FileNotFoundException("File not found: " + inputFile.getAbsolutePath());
        }

        System.out.print("Output file: ");
        File outputFile = new File(scanner.nextLine().trim());

        System.out.print("Enter key (non-empty): ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) throw new IllegalArgumentException("Key must be non-empty");

        FileManager.encryptFile(inputFile, outputFile, keyString);
        System.out.println("Encrypted saved to " + outputFile.getAbsolutePath());
    }

    private static void doDecryptFile() throws Exception {
        System.out.print("Input encrypted file: ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            throw new FileNotFoundException("File not found: " + inputFile.getAbsolutePath());
        }

        System.out.print("Output (decrypted) file: ");
        File outputFile = new File(scanner.nextLine().trim());

        System.out.print("Enter key (same as used for encryption): ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) throw new IllegalArgumentException("Key must be non-empty");

        FileManager.decryptFile(inputFile, outputFile, keyString);
        System.out.println("Decrypted saved to " + outputFile.getAbsolutePath());
    }

    private static void doTagCount() throws Exception {
        System.out.print("Enter URL (include http/https): ");
        String url = scanner.nextLine().trim();
        Map<String, Integer> frequencyMap = TagCounter.countTags(url);

        System.out.println("\nSorted by tag name:");
        List<Map.Entry<String, Integer>> byName = TagCounter.sortedByName(frequencyMap);
        byName.forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

        System.out.println("\nSorted by frequency (asc):");
        List<Map.Entry<String, Integer>> byFrequency = TagCounter.sortedByFrequency(frequencyMap);
        byFrequency.forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));

        System.out.print("Enter output file for tag frequency serialization: ");
        String outputPath = scanner.nextLine().trim();
        File outputFile = new File(outputPath);
        FileManager.serializeObject(outputFile, (Serializable) frequencyMap);
        System.out.println("Serialized tag frequency to: " + outputFile.getAbsolutePath());
    }

    private static void doFilterWriterDemo() throws Exception {
        System.out.print("Enter demo output file path: ");
        File demoOutputFile = new File(scanner.nextLine().trim());
        if (demoOutputFile.exists() && !demoOutputFile.canWrite()) {
            throw new IOException("Cannot write to file: " + demoOutputFile.getAbsolutePath());
        }

        System.out.print("Enter key: ");
        String keyString = scanner.nextLine();
        if (keyString == null || keyString.isEmpty()) throw new IllegalArgumentException("Key must be non-empty");

        System.out.println("Enter text (single line). Press ENTER to write:");
        String inputLine = scanner.nextLine();

        FileManager.writeEncryptedText(demoOutputFile, inputLine, keyString);
        System.out.println("Wrote encrypted text to " + demoOutputFile.getAbsolutePath());

        System.out.println("Now read back via CryptoFilterReader (decrypt):");
        String decrypted = FileManager.readEncryptedText(demoOutputFile, keyString);
        System.out.println("Decrypted content: " + decrypted);
    }

    private static void doDeserializeObject() throws Exception {
        System.out.print("Enter input file path for deserialization: ");
        File inputFile = new File(scanner.nextLine().trim());
        if (!inputFile.exists()) {
            throw new FileNotFoundException("File not found: " + inputFile.getAbsolutePath());
        }

        Object deserializedObject = FileManager.deserializeObject(inputFile);
        System.out.println("Deserialized object successfully.");
        System.out.println("Object class: " + deserializedObject.getClass().getName());
        System.out.println("Object data: " + deserializedObject);
    }
}
