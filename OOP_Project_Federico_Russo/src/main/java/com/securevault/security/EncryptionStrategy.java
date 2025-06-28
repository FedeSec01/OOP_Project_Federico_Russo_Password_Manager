package com.securevault.security;

/**
 * Interfaccia Strategy per algoritmi di crittografia.
 * Definisce una famiglia di algoritmi intercambiabili a runtime.
 * Permette di aggiungere nuovi algoritmi senza modificare il codice esistente.
 */
public interface EncryptionStrategy {

    /**
     * Cifra un testo in chiaro.
     * @param plaintext testo da cifrare
     * @return testo cifrato
     * @throws CryptoException in caso di errore di cifratura
     */
    String encrypt(String plaintext) throws CryptoException;

    /**
     * Decifra un testo cifrato.
     * @param ciphertext testo cifrato
     * @return testo in chiaro
     * @throws CryptoException in caso di errore di decifratura
     */
    String decrypt(String ciphertext) throws CryptoException;
}