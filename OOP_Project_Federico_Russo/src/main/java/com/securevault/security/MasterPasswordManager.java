package com.securevault.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MasterPasswordManager {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore generazione hash: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void saveMasterPassword(String password, String filePath) throws IOException {
        String hash = hashPassword(password);
        Files.writeString(Path.of(filePath), hash, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static boolean authenticate(String password, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            saveMasterPassword(password, filePath);
            return true;
        }

        String savedHash = Files.readString(Path.of(filePath)).trim();
        String inputHash = hashPassword(password);
        return savedHash.equals(inputHash);
    }
}