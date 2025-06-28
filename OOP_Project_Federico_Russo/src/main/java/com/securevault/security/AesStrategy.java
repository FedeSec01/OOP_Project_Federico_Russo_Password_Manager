package com.securevault.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

/**
 * Concrete Strategy per crittografia AES-256-GCM.
 * Implementa crittografia autenticata con protezione contro tampering.
 * NOTA: IV fisso per semplicità didattica - in produzione deve essere random!
 */
public class AesStrategy implements EncryptionStrategy {
    private static final String ALGO = "AES/GCM/NoPadding";    // Algoritmo AES in modalità GCM
    private final SecretKey key;                               // Chiave di cifratura AES-256

    /**
     * Costruttore che riceve la chiave di cifratura.
     * @param key chiave AES-256 generata da CryptoManager
     */
    public AesStrategy(SecretKey key) {
        this.key = key;
    }

    /**
     * Cifra il testo usando AES-256-GCM.
     * GCM fornisce sia confidenzialità che autenticazione.
     * @param plaintext testo in chiaro da cifrare
     * @return testo cifrato encodato in Base64
     * @throws CryptoException se la cifratura fallisce
     */
    @Override
    public String encrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            
            // NOTA: IV fisso per semplicità didattica
            // In produzione: byte[] iv = new SecureRandom().generateSeed(12);
            byte[] iv = new byte[12]; 
            
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);  // 128-bit authentication tag
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new CryptoException("AES encrypt failed", e);
        }
    }

    /**
     * Decifra il testo usando AES-256-GCM.
     * Verifica automaticamente l'autenticità del messaggio.
     * @param ciphertext testo cifrato in Base64
     * @return testo in chiaro
     * @throws CryptoException se la decifratura fallisce o il messaggio è stato alterato
     */
    @Override
    public String decrypt(String ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            
            // Stesso IV usato per la cifratura
            byte[] iv = new byte[12];
            
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(plaintext);
        } catch (Exception e) {
            throw new CryptoException("AES decrypt failed", e);
        }
    }
}
