package com.securevault.factory;

import com.securevault.model.Credential;

/**
 * Concrete Factory che crea istanze di Credential.
 * Implementa il pattern Factory nascondendo la logica di creazione al client.
 * Il client dipende solo dall'interfaccia CredentialInterface, non dalla classe concreta.
 */
public class CredentialFactory extends AbstractCredentialFactory {

    /**
     * Crea una nuova credenziale standard.
     * Potrebbe essere esteso in futuro con validazioni, cifratura, etc.
     * @param service nome del servizio
     * @param username username dell'utente
     * @param password password dell'utente
     * @return nuova istanza di Credential
     */
    @Override
    public CredentialInterface create(String service, String username, String password) {
        // Per ora crea solo Credential standard, ma la struttura è estendibile
        return new Credential(service, username, password);
    }
}
