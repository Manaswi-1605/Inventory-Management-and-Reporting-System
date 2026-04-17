package com.inventory;
import com.inventory.database_system.entity.Category;
import com.inventory.database_system.entity.Product;
import com.inventory.database_system.entity.Supplier;
import com.inventory.service.validation.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {

    private ProductValidator validator;

    // Builds a fully valid product — individual tests break one field at a time
    private Product validProduct() {
        Category cat = new Category();
        cat.setId(1L);

        Supplier sup = new Supplier();
        sup.setId(1L);

        Product p = new Product();
        p.setName("Keyboard");
        p.setSku("KB-001");
        p.setPrice(new BigDecimal("49.99"));
        p.setQuantity(50);
        p.setCategory(cat);
        p.setSupplier(sup);
        return p;
    }

    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    void validate_validProduct_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validate(validProduct()));
    }

    // ── Name validation ───────────────────────────────────────────────────────

    @Test
    void validate_nullName_throwsWithMessage() {
        Product p = validProduct();
        p.setName(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Product name required", ex.getMessage());
    }

    @Test
    void validate_blankName_throws() {
        Product p = validProduct();
        p.setName("   ");
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }

    // ── SKU validation ────────────────────────────────────────────────────────

    @Test
    void validate_nullSku_throwsWithMessage() {
        Product p = validProduct();
        p.setSku(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Product SKU required", ex.getMessage());
    }

    @Test
    void validate_blankSku_throws() {
        Product p = validProduct();
        p.setSku("");
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }

    // ── Price validation ──────────────────────────────────────────────────────

    @Test
    void validate_nullPrice_throwsWithMessage() {
        Product p = validProduct();
        p.setPrice(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Invalid price", ex.getMessage());
    }

    @Test
    void validate_zeroPrice_throws() {
        Product p = validProduct();
        p.setPrice(BigDecimal.ZERO);
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }

    @Test
    void validate_negativePrice_throws() {
        Product p = validProduct();
        p.setPrice(new BigDecimal("-1.00"));
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }

    // ── Quantity validation ───────────────────────────────────────────────────

    @Test
    void validate_negativeQuantity_throwsWithMessage() {
        Product p = validProduct();
        p.setQuantity(-1);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Invalid quantity", ex.getMessage());
    }

    @Test
    void validate_zeroQuantity_doesNotThrow() {
        // zero quantity is valid (out of stock, not negative)
        Product p = validProduct();
        p.setQuantity(0);
        assertDoesNotThrow(() -> validator.validate(p));
    }

    // ── Category validation ───────────────────────────────────────────────────

    @Test
    void validate_nullCategory_throwsWithMessage() {
        Product p = validProduct();
        p.setCategory(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Category is required", ex.getMessage());
    }

    @Test
    void validate_categoryWithNullId_throws() {
        Product p = validProduct();
        p.setCategory(new Category()); // id is null
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }

    // ── Supplier validation ───────────────────────────────────────────────────

    @Test
    void validate_nullSupplier_throwsWithMessage() {
        Product p = validProduct();
        p.setSupplier(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> validator.validate(p));
        assertEquals("Supplier is required", ex.getMessage());
    }

    @Test
    void validate_supplierWithNullId_throws() {
        Product p = validProduct();
        p.setSupplier(new Supplier()); // id is null
        assertThrows(RuntimeException.class, () -> validator.validate(p));
    }
}
