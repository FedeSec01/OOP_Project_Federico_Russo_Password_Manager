package com.securevault.exception;

public class CryptoOperationException extends VaultException {

    /**
     * Costruisce un'eccezione con solo messaggio.
     * @param message descrizione dell'errore crittografico
     */
    public CryptoOperationException(String message) {
        super(message);
    }

    /**
     * Costruisce un'eccezione con messaggio e causa.
     * @param message descrizione dell'errore crittografico
     * @param cause eccezione tecnica sottostante (es. NoSuchAlgorithmException)
     */
    public CryptoOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
