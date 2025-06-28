package com.securevault.util;

import com.securevault.model.Credential;
import com.securevault.security.CryptoException;
import com.securevault.security.CryptoManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Task per export CSV eseguito in background (Multithreading).
 * Implementa Runnable per essere eseguito in thread separato.
 * Evita di bloccare l'UI durante operazioni I/O intensive.
 */
public class CSVExportTask implements Runnable {
    private final List<Credential> creds;        // Dati da esportare
    private final String fileName;               // File di destinazione
    private final CryptoManager cryptoManager;   // Per cifratura password nel CSV

    /**
     * Costruttore del task.
     * 
     * @param creds lista credenziali da esportare
     * @param fileName nome file di destinazione
     * @param cryptoManager manager per cifratura
     */
    public CSVExportTask(List<Credential> creds, String fileName, CryptoManager cryptoManager) {
        this.creds = creds;
        this.fileName = fileName;
        this.cryptoManager = cryptoManager;
    }

    /**
     * Metodo principale eseguito dal thread.
     * Scrive CSV con password cifrate per sicurezza.
     */
    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            // Header CSV
            writer.println("Service,Username,EncryptedPassword");
            
            // Processo ogni credenziale
            for (Credential c : creds) {
                String encryptedPassword;
                try {
                    // SICUREZZA: cifra password prima di scriverla
                    encryptedPassword = cryptoManager.encrypt(c.getPassword());
                } catch (CryptoException e) {
                    // Gestione errore singola credenziale senza fermare tutto
                    System.err.println("Errore crittografia per " + c.getService() + ": " + e.getMessage());
                    encryptedPassword = "";  // Campo vuoto per errori
                }
                
                // Scrittura riga CSV
                writer.printf("%s,%s,%s%n",
                    c.getService(),
                    c.getUsername(),
                    encryptedPassword
                );
            }
            
            // Notifica completamento (thread-safe println)
            System.out.println("Esportazione CSV completata in: " + fileName);
            
        } catch (IOException e) {
            // Gestione errori I/O
            System.err.println("Errore I/O durante l'esportazione CSV: " + e.getMessage());
        }
    }
}