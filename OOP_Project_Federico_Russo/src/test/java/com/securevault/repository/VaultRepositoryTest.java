package com.securevault.repository;

import com.securevault.model.Credential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Set;

class VaultRepositoryTest {
    private VaultRepository repository;
    private Credential credential1;
    private Credential credential2;

    @BeforeEach
    void setUp() {
        repository = new VaultRepository();
        credential1 = new Credential("Gmail", "user1", "pass1");
        credential2 = new Credential("Slack", "user2", "pass2");
    }

    @Test
    void testAddCredential() {
        repository.addCredential("Personal", credential1);
        List<Credential> creds = repository.getByCategory("Personal");
        
        assertEquals(1, creds.size());
        assertTrue(creds.contains(credential1));
    }

    @Test
    void testGetCategories() {
        repository.addCredential("Personal", credential1);
        repository.addCredential("Work", credential2);
        
        Set<String> categories = repository.getCategories();
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Personal"));
        assertTrue(categories.contains("Work"));
    }

    @Test
    void testRemoveCredential() {
        repository.addCredential("Personal", credential1);
        repository.removeCredential("Personal", credential1);
        
        List<Credential> creds = repository.getByCategory("Personal");
        assertTrue(creds.isEmpty());
    }

    @Test
    void testCountAll() {
        repository.addCredential("Personal", credential1);
        repository.addCredential("Work", credential2);
        
        assertEquals(2, repository.countAll());
    }

    @Test
    void testIterator() {
        repository.addCredential("Personal", credential1);
        repository.addCredential("Work", credential2);
        
        int count = 0;
        for (Credential cred : repository) {
            assertNotNull(cred);
            count++;
        }
        assertEquals(2, count);
    }
}