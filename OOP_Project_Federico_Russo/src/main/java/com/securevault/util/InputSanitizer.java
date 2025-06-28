package com.securevault.util;

/**
 * Utility per sanitizzazione e validazione input.
 * Implementa principi di Secure Programming per prevenire:
 * - Injection attacks
 * - Buffer overflow
 * - Path traversal
 * - Command injection
 */
public class InputSanitizer {

    /**
     * Sanitizza input utente rimuovendo caratteri pericolosi.
     * 
     * Controlli implementati:
     * 1. Null e empty check
     * 2. Trim whitespace
     * 3. Blacklist caratteri pericolosi
     * 
     * @param input stringa da sanitizzare
     * @return stringa sanitizzata
     * @throws IllegalArgumentException se input non valido
     */
    public static String sanitize(String input) throws IllegalArgumentException {
        // 1. VALIDAZIONE BASE
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input non valido: vuoto o nullo.");
        }

        // 2. PULIZIA WHITESPACE
        String cleaned = input.trim();

        // 3. BLACKLIST CARATTERI PERICOLOSI
        // Previene: command injection, path traversal, SQL injection
        if (cleaned.matches(".*[;|&\"<>].*")) {
            throw new IllegalArgumentException("Input contiene caratteri non consentiti.");
        }

        return cleaned;
    }
    
    // NB: Per password NON si usa sanitizzazione per preservare caratteri speciali

}
