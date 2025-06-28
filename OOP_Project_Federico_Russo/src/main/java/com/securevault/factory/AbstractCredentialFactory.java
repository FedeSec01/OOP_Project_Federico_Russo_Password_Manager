package com.securevault.factory;

/**
 * Abstract Factory per la creazione di credenziali.
 * Definisce il template per diverse tipologie di factory.
 * Permette estensioni future (es. CredentialFactoryEncrypted, CredentialFactoryLDAP, etc.)
 */
public abstract class AbstractCredentialFactory {
    /**
     * Metodo factory astratto che deve essere implementato dalle sottoclassi.
     * @param service nome del servizio
     * @param username username dell'utente  
     * @param password password dell'utente
     * @return nuova istanza di CredentialInterface
     */
    public abstract CredentialInterface create(String service, String username, String password);
}
