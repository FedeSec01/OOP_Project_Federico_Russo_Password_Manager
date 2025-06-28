package com.securevault.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputSanitizerTest {

    @Test
    void testValidInput() {
        String result = InputSanitizer.sanitize("ValidInput123");
        assertEquals("ValidInput123", result);
    }

    @Test
    void testInputWithSpaces() {
        String result = InputSanitizer.sanitize("  Input with spaces  ");
        assertEquals("Input with spaces", result);
    }

    @Test
    void testNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputSanitizer.sanitize(null);
        });
    }

    @Test
    void testEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputSanitizer.sanitize("");
        });
    }

    @Test
    void testInputWithForbiddenCharacters() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputSanitizer.sanitize("input;with&forbidden<chars>");
        });
    }

    @Test
    void testOnlyWhitespaceInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            InputSanitizer.sanitize("   ");
        });
    }
}