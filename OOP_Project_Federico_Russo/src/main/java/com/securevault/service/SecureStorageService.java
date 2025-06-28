package com.securevault.service;

import com.securevault.exception.ExceptionHandler;
import com.securevault.exception.VaultException;
import com.securevault.model.Credential;
import com.securevault.security.CryptoManager;

import javax.crypto.SecretKey;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SecureStorageService {
    private static final Logger logger = Logger.getLogger(SecureStorageService.class.getName());

    private SecretKey key;
    private final String storagePath;
    private CryptoManager cryptoManager;

    public SecureStorageService(String storagePath) {
        this.storagePath = storagePath;
    }

    public void setEncryptionKey(SecretKey key) {
        this.key = key;
        this.cryptoManager = new CryptoManager(key);
    }

    public SecretKey getEncryptionKey() {
        return key;
    }

    public void loadKeyFromFile(File keyFile) {
        try (FileInputStream fis = new FileInputStream(keyFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.key = (SecretKey) ois.readObject();
            this.cryptoManager = new CryptoManager(key);
        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Errore nel caricamento della chiave: " + e.getMessage());
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
    }

    public void saveKeyToFile(File keyFile) {
        try (FileOutputStream fos = new FileOutputStream(keyFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(key);
        } catch (IOException e) {
            logger.severe("Errore nel salvataggio della chiave: " + e.getMessage());
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
    }

    public List<Credential> loadCredentials() {
        List<Credential> list = new ArrayList<>();
        File file = new File(storagePath);
        if (!file.exists()) return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    // Decrittografa la riga
                    String decryptedLine = cryptoManager.decrypt(line.trim());
                    list.add(Credential.deserialize(decryptedLine));
                } catch (Exception e) {
                    logger.warning("Riga non decrittografabile ignorata: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Errore nella lettura del vault: " + e.getMessage());
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
        return list;
    }

    public void overwriteAll(List<Credential> creds) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(storagePath, false))) {
            for (Credential c : creds) {
                // Crittografa ogni riga prima di salvarla
                String serializedCred = c.serialize();
                String encryptedLine = cryptoManager.encrypt(serializedCred);
                writer.println(encryptedLine);
            }
        } catch (Exception e) {
            logger.severe("Errore scrittura vault: " + e.getMessage());
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
    }

    public void saveCredential(Credential cred) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(storagePath, true))) {
            // Crittografa la credenziale prima di salvarla
            String serializedCred = cred.serialize();
            String encryptedLine = cryptoManager.encrypt(serializedCred);
            writer.println(encryptedLine);
        } catch (Exception e) {
            logger.severe("Errore salvataggio credenziale: " + e.getMessage());
            throw new VaultException(ExceptionHandler.handleException(e), e);
        }
    }
}