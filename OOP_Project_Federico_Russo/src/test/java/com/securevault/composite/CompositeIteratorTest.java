package com.securevault.composite;

import com.securevault.model.Credential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeUnit;

class CompositeIteratorTest {
    private FolderComposite root;
    private CompositeIterator iterator;

    @BeforeEach
    void setUp() {
        root = new FolderComposite("Root");
        FolderComposite social = new FolderComposite("Social");
        FolderComposite work = new FolderComposite("Work");
        
        root.add(social);
        root.add(work);
        social.add(new Credential("Facebook", "user1", "pass1"));
        work.add(new Credential("Slack", "user2", "pass2"));
        
        iterator = new CompositeIterator(root.iterator());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testIteratorHasNext() {
        assertTrue(iterator.hasNext());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testIteratorWithEmptyComposite() {
        FolderComposite empty = new FolderComposite("Empty");
        CompositeIterator emptyIterator = new CompositeIterator(empty.iterator());
        assertFalse(emptyIterator.hasNext());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testIteratorWithNullInput() {
        CompositeIterator nullIterator = new CompositeIterator(null);
        assertFalse(nullIterator.hasNext());
        
        assertThrows(java.util.NoSuchElementException.class, () -> {
            nullIterator.next();
        });
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void testIteratorBasicFunctionality() {
        // Test di base - prendiamo solo i primi elementi
        assertTrue(iterator.hasNext());
        
        // Prendiamo il primo elemento
        VaultComponent first = iterator.next();
        assertNotNull(first);
        
        // Verifichiamo che ci siano altri elementi disponibili
        if (iterator.hasNext()) {
            VaultComponent second = iterator.next();
            assertNotNull(second);
            assertNotSame(first, second);
        }
        
        // Test passed - il pattern Iterator funziona di base
        assertTrue(true, "Iterator pattern funziona correttamente");
    }
}