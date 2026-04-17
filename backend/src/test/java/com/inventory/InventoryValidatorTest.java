package com.inventory;

import com.inventory.service.validation.InventoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryValidatorTest {

    private InventoryValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InventoryValidator();
    }

    // ── validateQuantity ─────────────────────────────────────────────────────

    @Test
    void validateQuantity_positive_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateQuantity(1));
        assertDoesNotThrow(() -> validator.validateQuantity(100));
    }

    @Test
    void validateQuantity_zero_throws() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validateQuantity(0));
        assertEquals("Quantity must be greater than 0", ex.getMessage());
    }

    @Test
    void validateQuantity_negative_throws() {
        assertThrows(RuntimeException.class, () -> validator.validateQuantity(-5));
    }

    // ── validateStock ─────────────────────────────────────────────────────────

    @Test
    void validateStock_sufficientStock_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateStock(100, 50));
        assertDoesNotThrow(() -> validator.validateStock(50, 50)); // exact match is fine
    }

    @Test
    void validateStock_insufficientStock_throwsWithMessage() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validateStock(10, 20));
        assertTrue(ex.getMessage().contains("Insufficient stock"));
        assertTrue(ex.getMessage().contains("10"));  // available
        assertTrue(ex.getMessage().contains("20"));  // requested
    }

    @Test
    void validateStock_zeroStock_throws() {
        assertThrows(RuntimeException.class, () -> validator.validateStock(0, 1));
    }

    // ── validateProductExists ─────────────────────────────────────────────────

    @Test
    void validateProductExists_nonNullProduct_doesNotThrow() {
        Object product = new Object();
        assertDoesNotThrow(() -> validator.validateProductExists(product, 1));
    }

    @Test
    void validateProductExists_nullProduct_throwsWithId() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validateProductExists(null, 42));
        assertTrue(ex.getMessage().contains("42"));
    }
}
