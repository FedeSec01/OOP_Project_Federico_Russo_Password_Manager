package com.securevault.observer;

import com.securevault.model.Credential;

/**
 * Interfaccia Observer del Pattern Observer.
 * Definisce i metodi che gli observer devono implementare per ricevere notifiche.
 * Ogni metodo corrisponde a un evento specifico del vault.
 */
public interface VaultObserver {
    
    /**
     * Notifica quando viene aggiunta una credenziale.
     * @param credential credenziale aggiunta
     * @param category categoria in cui è stata aggiunta
     */
    void onCredentialAdded(Credential credential, String category);
    
    /**
     * Notifica quando viene rimossa una credenziale.
     * @param credential credenziale rimossa  
     * @param category categoria da cui è stata rimossa
     */
    void onCredentialRemoved(Credential credential, String category);
    
    /**
     * Notifica quando viene modificata una credenziale.
     * @param oldCredential credenziale prima della modifica
     * @param newCredential credenziale dopo la modifica
     * @param category categoria della credenziale
     */
    void onCredentialModified(Credential oldCredential, Credential newCredential, String category);
    
    /**
     * Notifica quando il vault viene completamente svuotato.
     */
    void onVaultCleared();
}