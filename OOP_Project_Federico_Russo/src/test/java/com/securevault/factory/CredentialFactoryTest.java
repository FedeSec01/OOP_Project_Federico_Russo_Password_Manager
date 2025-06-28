package com.securevault.factory;

import com.securevault.model.Credential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CredentialFactoryTest {
    private CredentialFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CredentialFactory();
    }

    @Test
    void testCreateCredential() {
        CredentialInterface cred = factory.create("Gmail", "user@test.com", "password123");
        
        assertNotNull(cred);
        assertEquals("Gmail", cred.getService());
        assertEquals("user@test.com", cred.getUsername());
        assertEquals("password123", cred.getPassword());
    }

    @Test
    void testCreateCredentialWithEmptyValues() {
        CredentialInterface cred = factory.create("", "", "");
        
        assertNotNull(cred);
        assertEquals("", cred.getService());
        assertEquals("", cred.getUsername());
        assertEquals("", cred.getPassword());
    }

    @Test
    void testFactoryReturnsCredentialInstance() {
        CredentialInterface cred = factory.create("Test", "test", "test");
        assertTrue(cred instanceof Credential);
    }
}