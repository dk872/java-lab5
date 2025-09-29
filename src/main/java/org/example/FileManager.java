package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager {

    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lines.add(currentLine);
            }
        }

        return lines;
    }

    public static void serializeObject(File outFile, Serializable obj) throws IOException {
        try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(outFile))) {
            objectOut.writeObject(obj);
        }
    }

    public static Object deserializeObject(File inFile) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(inFile))) {
            return objectIn.readObject();
        }
    }

    public static void encryptFile(File inputFile, File outputFile, String key) throws IOException {
        try (BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            CipherUtils.encryptStream(bufferedIn, bufferedOut, key);
        }
    }

    public static void decryptFile(File inputFile, File outputFile, String key) throws IOException {
        try (BufferedInputStream bufferedIn = new BufferedInputStream(new FileInputStream(inputFile));
             BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            CipherUtils.decryptStream(bufferedIn, bufferedOut, key);
        }
    }

    public static void writeEncryptedText(File outputFile, String text, String key) throws IOException {
        try (CryptoFilterWriter filterWriter = new CryptoFilterWriter(new FileWriter(outputFile,
                StandardCharsets.UTF_8), key)) {
            filterWriter.write(text);
        }
    }

    public static String readEncryptedText(File inputFile, String key) throws IOException {
        try (CryptoFilterReader filterReader = new CryptoFilterReader(new FileReader(inputFile,
                StandardCharsets.UTF_8), key)) {
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[2048];

            int readCount;
            while ((readCount = filterReader.read(buffer)) != -1) builder.append(buffer, 0, readCount);

            return builder.toString();
        }
    }
}
