package com.inventory;
import com.inventory.dao.ProductDAO;
import com.inventory.database_system.entity.Category;
import com.inventory.database_system.entity.Product;
import com.inventory.database_system.entity.Supplier;
import com.inventory.service.ProductService;
import com.inventory.service.validation.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ProductValidator validator;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Electronics");

        Supplier sup = new Supplier();
        sup.setId(1L);
        sup.setName("TechSupplier");

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Wireless Mouse");
        sampleProduct.setSku("WM-001");
        sampleProduct.setPrice(new BigDecimal("29.99"));
        sampleProduct.setQuantity(100);
        sampleProduct.setReorderLevel(10);
        sampleProduct.setActive(true);
        sampleProduct.setCategory(cat);
        sampleProduct.setSupplier(sup);
    }

    // ── addProduct ────────────────────────────────────────────────────────────

    @Test
    void addProduct_validProduct_callsValidatorAndDAO() {
        productService.addProduct(sampleProduct);

        verify(validator, times(1)).validate(sampleProduct);
        verify(productDAO, times(1)).addProduct(sampleProduct);
    }

    @Test
    void addProduct_validationFails_doesNotCallDAO() {
        doThrow(new RuntimeException("Invalid price"))
                .when(validator).validate(sampleProduct);

        assertThrows(RuntimeException.class, () -> productService.addProduct(sampleProduct));
        verify(productDAO, never()).addProduct(any());
    }

    // ── getAllProducts ────────────────────────────────────────────────────────

    @Test
    void getAllProducts_returnsListFromDAO() {
        when(productDAO.getAllProducts()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Wireless Mouse", result.get(0).getName());
        verify(productDAO, times(1)).getAllProducts();
    }

    @Test
    void getAllProducts_emptyList_returnsEmpty() {
        when(productDAO.getAllProducts()).thenReturn(List.of());

        List<Product> result = productService.getAllProducts();

        assertTrue(result.isEmpty());
    }

    // ── getActiveProducts ─────────────────────────────────────────────────────

    @Test
    void getActiveProducts_returnsOnlyActiveFromDAO() {
        when(productDAO.getActiveProducts()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getActiveProducts();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
    }

    // ── getProduct ────────────────────────────────────────────────────────────

    @Test
    void getProduct_existingId_returnsProduct() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        Product result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("WM-001", result.getSku());
    }

    @Test
    void getProduct_nonExistingId_throwsRuntimeException() {
        when(productDAO.getProductById(99L))
                .thenThrow(new RuntimeException("Product not found with id: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.getProduct(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ── searchByName ──────────────────────────────────────────────────────────

    @Test
    void searchByName_matchingName_returnsResults() {
        when(productDAO.searchByName("mouse")).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.searchByName("mouse");

        assertEquals(1, result.size());
        assertEquals("Wireless Mouse", result.get(0).getName());
    }

    @Test
    void searchByName_noMatch_returnsEmptyList() {
        when(productDAO.searchByName("xyz")).thenReturn(List.of());

        List<Product> result = productService.searchByName("xyz");

        assertTrue(result.isEmpty());
    }

    // ── getProductsByCategory ─────────────────────────────────────────────────

    @Test
    void getProductsByCategory_returnsMatchingProducts() {
        when(productDAO.getProductsByCategory(1L)).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getProductsByCategory(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCategory().getId());
    }

    // ── getProductsBySupplier ─────────────────────────────────────────────────

    @Test
    void getProductsBySupplier_returnsMatchingProducts() {
        when(productDAO.getProductsBySupplier(1L)).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getProductsBySupplier(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSupplier().getId());
    }

    // ── deleteProduct ─────────────────────────────────────────────────────────

    @Test
    void deleteProduct_callsDAO() {
        productService.deleteProduct(1L);

        verify(productDAO, times(1)).deleteProduct(1L);
    }

    // ── deactivateProduct ─────────────────────────────────────────────────────

    @Test
    void deactivateProduct_callsDAO() {
        productService.deactivateProduct(1L);

        verify(productDAO, times(1)).deactivateProduct(1L);
    }
}
