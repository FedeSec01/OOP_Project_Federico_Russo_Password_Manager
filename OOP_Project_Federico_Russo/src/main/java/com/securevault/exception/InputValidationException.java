package com.securevault.exception;

public class InputValidationException extends VaultException {

    /**
     * Costruisce un'eccezione con solo messaggio.
     * @param message descrizione del problema di validazione
     */
    public InputValidationException(String message) {
        super(message);
    }

    /**
     * Costruisce un'eccezione con messaggio e causa tecnica.
     * @param message descrizione del problema di validazione
     * @param cause eccezione tecnica sottostante
     */
    public InputValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
