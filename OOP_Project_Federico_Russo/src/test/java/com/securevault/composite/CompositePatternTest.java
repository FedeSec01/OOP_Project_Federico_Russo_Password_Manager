package com.securevault.composite;

import com.securevault.model.Credential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositePatternTest {
    private FolderComposite root;
    private FolderComposite social;
    private Credential credential;

    @BeforeEach
    void setUp() {
        root = new FolderComposite("Root");
        social = new FolderComposite("Social");
        credential = new Credential("Facebook", "user", "pass");
    }

    @Test
    void testFolderCreation() {
        assertEquals("Root", root.getName());
        assertEquals("Social", social.getName());
    }

    @Test
    void testAddChildToFolder() {
        root.add(social);
        social.add(credential);
        
        // Test iterator functionality
        boolean foundSocial = false;
        for (VaultComponent component : root) {
            if (component.getName().equals("Social")) {
                foundSocial = true;
                break;
            }
        }
        assertTrue(foundSocial);
    }

    @Test
    void testCredentialAsLeaf() {
        assertEquals("Facebook - user", credential.getName());
        
        // Credential should throw exception when trying to add children
        assertThrows(UnsupportedOperationException.class, () -> {
            credential.add(new Credential("Test", "test", "test"));
        });
    }

    @Test
    void testRemoveFromFolder() {
        social.add(credential);
        social.remove(credential);
        
        // Should be empty after removal
        assertFalse(social.iterator().hasNext());
    }
}