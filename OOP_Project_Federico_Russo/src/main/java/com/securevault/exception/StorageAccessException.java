package com.securevault.exception;

public class StorageAccessException extends VaultException {

    /**
     * Costruisce un'eccezione con solo messaggio.
     * @param message descrizione dell'errore di storage
     */
    public StorageAccessException(String message) {
        super(message);
    }

    /**
     * Costruisce un'eccezione con messaggio e causa tecnica.
     * @param message descrizione dell'errore di storage
     * @param cause eccezione tecnica sottostante (es. IOException)
     */
    public StorageAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
