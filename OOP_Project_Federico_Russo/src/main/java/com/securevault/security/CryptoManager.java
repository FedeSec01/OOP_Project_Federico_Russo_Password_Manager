package com.securevault.security;

import com.securevault.exception.CryptoException;
import com.securevault.exception.ExceptionHandler;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Context del Strategy Pattern per la gestione della crittografia.
 * Coordina l'uso delle strategie di crittografia e fornisce utility per le chiavi.
 * Nasconde al client i dettagli delle strategie concrete.
 */
public class CryptoManager {
    private final EncryptionStrategy strategy;  // Strategia di crittografia corrente

    /**
     * Costruttore con strategia specifica.
     * Permette di iniettare qualsiasi strategia che implementi EncryptionStrategy.
     * @param strategy strategia di crittografia da utilizzare
     */
    public CryptoManager(EncryptionStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Costruttore di convenienza che crea automaticamente AesStrategy.
     * @param key chiave AES per la strategia di default
     */
    public CryptoManager(SecretKey key) {
        this(new AesStrategy(key));
    }

    /**
     * Cifra il testo delegando alla strategia corrente.
     * Pattern delegation - il Context delega alla Strategy.
     * @param plaintext testo da cifrare
     * @return testo cifrato
     * @throws CryptoException in caso di errore
     */
    public String encrypt(String plaintext) {
        try {
            return strategy.encrypt(plaintext);
        } catch (Exception e) {
            // Exception Shielding - converte eccezioni tecniche in eccezioni di dominio
            throw new CryptoException(ExceptionHandler.handleException(e), e);
        }
    }

    /**
     * Decifra il testo delegando alla strategia corrente.
     * @param ciphertext testo cifrato
     * @return testo in chiaro
     * @throws CryptoException in caso di errore
     */
    public String decrypt(String ciphertext) {
        try {
            return strategy.decrypt(ciphertext);
        } catch (Exception e) {
            // Exception Shielding pattern
            throw new CryptoException(ExceptionHandler.handleException(e), e);
        }
    }

    // ---- UTILITY METHODS PER GESTIONE CHIAVI (STATIC) ----

    /**
     * Genera una nuova chiave AES-256 casualia.
     * Metodo statico per essere usato indipendentemente dalla strategia.
     * @return nuova chiave AES-256
     * @throws CryptoException se la generazione fallisce
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);  // AES-256
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(ExceptionHandler.handleException(e), e);
        }
    }

    /**
     * Codifica una chiave in stringa Base64 per il salvataggio.
     * @param key chiave da codificare
     * @return stringa Base64 della chiave
     */
    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Decodifica una chiave da stringa Base64.
     * @param encoded stringa Base64 della chiave
     * @return chiave AES ricostruita
     * @throws CryptoException se la decodifica fallisce
     */
    public static SecretKey decodeKey(String encoded) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encoded);
            return new SecretKeySpec(decoded, "AES");
        } catch (Exception e) {
            throw new CryptoException(ExceptionHandler.handleException(e), e);
        }
    }
}
