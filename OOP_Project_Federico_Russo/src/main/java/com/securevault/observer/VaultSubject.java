package com.securevault.observer;

import com.securevault.model.Credential;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Subject (Observable) del Pattern Observer.
 * Gestisce la lista degli observer e fornisce metodi per notificarli.
 * È abstract per permettere alle sottoclassi di decidere quando notificare.
 */
public abstract class VaultSubject {
    // Lista degli observer registrati
    private final List<VaultObserver> observers = new ArrayList<>();

    /**
     * Registra un nuovo observer.
     * @param observer observer da registrare
     */
    public void addObserver(VaultObserver observer) {
        observers.add(observer);
    }

    /**
     * Rimuove un observer dalla lista.
     * @param observer observer da rimuovere
     */
    public void removeObserver(VaultObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica tutti gli observer dell'aggiunta di una credenziale.
     * Metodo protected per essere chiamato dalle sottoclassi.
     * @param credential credenziale aggiunta
     * @param category categoria di aggiunta
     */
    protected void notifyCredentialAdded(Credential credential, String category) {
        // Itera su tutti gli observer e li notifica
        for (VaultObserver observer : observers) {
            observer.onCredentialAdded(credential, category);
        }
    }

    /**
     * Notifica tutti gli observer della rimozione di una credenziale.
     * @param credential credenziale rimossa
     * @param category categoria di rimozione
     */
    protected void notifyCredentialRemoved(Credential credential, String category) {
        for (VaultObserver observer : observers) {
            observer.onCredentialRemoved(credential, category);
        }
    }

    /**
     * Notifica tutti gli observer della modifica di una credenziale.
     * @param oldCredential credenziale prima della modifica
     * @param newCredential credenziale dopo la modifica
     * @param category categoria della credenziale
     */
    protected void notifyCredentialModified(Credential oldCredential, Credential newCredential, String category) {
        for (VaultObserver observer : observers) {
            observer.onCredentialModified(oldCredential, newCredential, category);
        }
    }

    /**
     * Notifica tutti gli observer dello svuotamento del vault.
     */
    protected void notifyVaultCleared() {
        for (VaultObserver observer : observers) {
            observer.onVaultCleared();
        }
    }
}