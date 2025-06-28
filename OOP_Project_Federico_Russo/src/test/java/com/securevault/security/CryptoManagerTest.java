package com.securevault.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.crypto.SecretKey;

class CryptoManagerTest {
    private CryptoManager cryptoManager;
    private static final String TEST_PLAINTEXT = "TestPassword123!";

    @BeforeEach
    void setUp() {
        SecretKey key = CryptoManager.generateKey();
        cryptoManager = new CryptoManager(key);
    }

    @Test
    void testEncryptDecrypt() {
        String encrypted = cryptoManager.encrypt(TEST_PLAINTEXT);
        assertNotNull(encrypted);
        assertNotEquals(TEST_PLAINTEXT, encrypted);
        
        String decrypted = cryptoManager.decrypt(encrypted);
        assertEquals(TEST_PLAINTEXT, decrypted);
    }

    @Test
    void testGenerateKey() {
        SecretKey key = CryptoManager.generateKey();
        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void testEncodeDecodeKey() {
        SecretKey originalKey = CryptoManager.generateKey();
        String encoded = CryptoManager.encodeKey(originalKey);
        assertNotNull(encoded);
        
        SecretKey decodedKey = CryptoManager.decodeKey(encoded);
        assertArrayEquals(originalKey.getEncoded(), decodedKey.getEncoded());
    }

    @Test
    void testDummyStrategy() {
        CryptoManager dummyManager = new CryptoManager(new DummyStrategy());
        String encrypted = dummyManager.encrypt("hello");
        String decrypted = dummyManager.decrypt(encrypted);
        assertEquals("hello", decrypted);
    }
}