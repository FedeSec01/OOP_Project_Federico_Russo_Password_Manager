package com.securevault.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementa il Pattern Exception Shielding.
 * Converte eccezioni tecniche in messaggi user-friendly per prevenire information leakage.
 * Registra i dettagli tecnici nei log per debugging mantenendo la sicurezza.
 */
public class ExceptionHandler {
    private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getName());

    /**
     * Elabora un'eccezione applicando Exception Shielding.
     * 
     * Processo:
     * 1. Logga l'eccezione completa con stack trace per debugging
     * 2. Classifica l'eccezione per tipo
     * 3. Restituisce un messaggio sanitizzato per l'utente finale
     * 
     * @param e eccezione da gestire
     * @return messaggio sanitizzato sicuro per l'utente
     */
    public static String handleException(Exception e) {
        // 1. LOGGING COMPLETO per amministratori/sviluppatori
        logger.log(Level.SEVERE, "Errore gestito dal vault: " + e.getMessage(), e);

        // 2. CLASSIFICAZIONE E SHIELDING per utenti finali
        if (e instanceof NullPointerException) {
            // NPE indica errore di programmazione - messaggio generico
            return "Errore interno: riferimento mancante.";
        } else if (e instanceof IllegalArgumentException) {
            // Input non valido - possiamo essere più specifici
            return "Parametro non valido.";
        } else if (e instanceof IllegalStateException) {
            // Stato inconsistente dell'applicazione
            return "Stato non coerente dell'applicazione.";
        } else if (e instanceof RuntimeException) {
            // Altre RuntimeException - messaggio generico
            return "Errore generico dell'applicazione.";
        } else {
            // Checked exception o tipo sconosciuto
            return "Errore sconosciuto. Contattare l'amministratore.";
        }
    }
}
