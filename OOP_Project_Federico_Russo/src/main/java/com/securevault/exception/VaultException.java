package com.securevault.exception;

public class VaultException extends RuntimeException {

    /**
     * Costruisce una VaultException con solo messaggio.
     * @param message messaggio di errore da visualizzare/loggare
     */
    public VaultException(String message) {
        super(message);
    }

    /**
     * Costruisce una VaultException con messaggio e causa sottostante.
     * @param message messaggio di errore
     * @param cause eccezione sottostante
     */
    public VaultException(String message, Throwable cause) {
        super(message, cause);
    }
}
