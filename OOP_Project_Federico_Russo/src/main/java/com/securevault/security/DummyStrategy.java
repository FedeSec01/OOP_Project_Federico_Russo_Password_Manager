package com.securevault.security;

public class DummyStrategy implements EncryptionStrategy {
    public DummyStrategy() {} 

    @Override
    public String encrypt(String plaintext) {
        return new StringBuilder(plaintext).reverse().toString();
    }

    @Override
    public String decrypt(String ciphertext) {
        return new StringBuilder(ciphertext).reverse().toString();
    }
}