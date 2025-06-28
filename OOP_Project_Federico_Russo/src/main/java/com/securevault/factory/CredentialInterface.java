package com.securevault.factory;

/**
 * Interfaccia Product del Factory Pattern.
 * Definisce il contratto per tutti i tipi di credenziali.
 * Permette al Factory di restituire oggetti senza esporre le classi concrete.
 */
public interface CredentialInterface {
    /**
     * @return nome del servizio/applicazione
     */
    String getService();
    
    /**
     * @return username/email per il servizio
     */
    String getUsername();
    
    /**
     * @return password per il servizio
     */
    String getPassword();
}