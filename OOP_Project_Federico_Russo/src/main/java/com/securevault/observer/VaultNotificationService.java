package com.securevault.observer;

import com.securevault.model.Credential;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Servizio di notificazione per eventi del vault
 * Implementa il pattern Observer per tracciare modifiche
 */
public class VaultNotificationService implements VaultObserver {
    
    private static final Logger logger = Logger.getLogger(VaultNotificationService.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private boolean consoleOutput = true;
    private int eventCount = 0;

    public VaultNotificationService() {
        this(true);
    }

    public VaultNotificationService(boolean enableConsoleOutput) {
        this.consoleOutput = enableConsoleOutput;
    }

    @Override
    public void onCredentialAdded(Credential credential, String category) {
        eventCount++;
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("[%s] ✅ Aggiunta credenziale: %s in categoria '%s'", 
                                     timestamp, credential.getName(), category);
        
        if (consoleOutput) {
            System.out.println(message);
        }
        logger.info("Credential added: " + credential.getService() + " in " + category);
    }

    @Override
    public void onCredentialRemoved(Credential credential, String category) {
        eventCount++;
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("[%s] ❌ Rimossa credenziale: %s da categoria '%s'", 
                                     timestamp, credential.getName(), category);
        
        if (consoleOutput) {
            System.out.println(message);
        }
        logger.info("Credential removed: " + credential.getService() + " from " + category);
    }

    @Override
    public void onCredentialModified(Credential oldCredential, Credential newCredential, String category) {
        eventCount++;
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("[%s] 🔄 Modificata credenziale: %s → %s in categoria '%s'", 
                                     timestamp, oldCredential.getName(), newCredential.getName(), category);
        
        if (consoleOutput) {
            System.out.println(message);
        }
        logger.info("Credential modified: " + oldCredential.getName() + " → " + newCredential.getName());
    }

    @Override
    public void onVaultCleared() {
        eventCount++;
        String timestamp = LocalDateTime.now().format(formatter);
        String message = String.format("[%s] 🗑️ Vault completamente svuotato", timestamp);
        
        if (consoleOutput) {
            System.out.println(message);
        }
        logger.warning("Vault cleared - all credentials removed");
    }

    /**
     * Abilita o disabilita l'output su console
     * @param enabled true per abilitare l'output
     */
    public void setConsoleOutput(boolean enabled) {
        this.consoleOutput = enabled;
    }

    /**
     * Restituisce il numero di eventi processati
     * @return numero di eventi
     */
    public int getEventCount() {
        return eventCount;
    }

    /**
     * Reset del contatore eventi
     */
    public void resetEventCount() {
        this.eventCount = 0;
    }

    /**
     * Mostra statistiche del servizio
     */
    public void showStatistics() {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.printf("[%s] 📊 Eventi processati: %d%n", timestamp, eventCount);
    }
}