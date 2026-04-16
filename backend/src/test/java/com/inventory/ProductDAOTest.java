package com.inventory;

import com.inventory.dao.ProductDAO;
import com.inventory.database_system.entity.Category;
import com.inventory.database_system.entity.Product;
import com.inventory.database_system.entity.Supplier;
import com.inventory.database_system.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDAOTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductDAO productDAO;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        Category cat = new Category();
        cat.setId(1L);

        Supplier sup = new Supplier();
        sup.setId(1L);

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Monitor");
        sampleProduct.setSku("MON-001");
        sampleProduct.setPrice(new BigDecimal("199.99"));
        sampleProduct.setQuantity(30);
        sampleProduct.setReorderLevel(5);
        sampleProduct.setActive(true);
        sampleProduct.setCategory(cat);
        sampleProduct.setSupplier(sup);
    }

    // ── addProduct ────────────────────────────────────────────────────────────

    @Test
    void addProduct_callsRepositorySave() {
        productDAO.addProduct(sampleProduct);
        verify(productRepository, times(1)).save(sampleProduct);
    }

    // ── getAllProducts ────────────────────────────────────────────────────────

    @Test
    void getAllProducts_returnsAllFromRepository() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Monitor", result.get(0).getName());
    }

    // ── getProductById ────────────────────────────────────────────────────────

    @Test
    void getProductById_found_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productDAO.getProductById(1L);

        assertNotNull(result);
        assertEquals("MON-001", result.getSku());
    }

    @Test
    void getProductById_notFound_throwsRuntimeException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productDAO.getProductById(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ── findBySku ─────────────────────────────────────────────────────────────

    @Test
    void findBySku_existingSku_returnsOptionalWithProduct() {
        when(productRepository.findBySku("MON-001")).thenReturn(Optional.of(sampleProduct));

        Optional<Product> result = productDAO.findBySku("MON-001");

        assertTrue(result.isPresent());
        assertEquals("Monitor", result.get().getName());
    }

    @Test
    void findBySku_nonExistingSku_returnsEmpty() {
        when(productRepository.findBySku("NONE")).thenReturn(Optional.empty());

        Optional<Product> result = productDAO.findBySku("NONE");

        assertFalse(result.isPresent());
    }

    // ── existsBySku ───────────────────────────────────────────────────────────

    @Test
    void existsBySku_existingSku_returnsTrue() {
        when(productRepository.existsBySku("MON-001")).thenReturn(true);
        assertTrue(productDAO.existsBySku("MON-001"));
    }

    @Test
    void existsBySku_nonExistingSku_returnsFalse() {
        when(productRepository.existsBySku("NONE")).thenReturn(false);
        assertFalse(productDAO.existsBySku("NONE"));
    }

    // ── getActiveProducts ─────────────────────────────────────────────────────

    @Test
    void getActiveProducts_returnsOnlyActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.getActiveProducts();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
    }

    // ── getProductsByCategory ─────────────────────────────────────────────────

    @Test
    void getProductsByCategory_returnsMatchingProducts() {
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.getProductsByCategory(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCategory().getId());
    }

    // ── getProductsBySupplier ─────────────────────────────────────────────────

    @Test
    void getProductsBySupplier_returnsMatchingProducts() {
        when(productRepository.findBySupplierId(1L)).thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.getProductsBySupplier(1L);

        assertEquals(1, result.size());
    }

    // ── searchByName ──────────────────────────────────────────────────────────

    @Test
    void searchByName_partialMatch_returnsResults() {
        when(productRepository.findByNameContainingIgnoreCase("mon"))
                .thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.searchByName("mon");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("mon"));
    }

    // ── getLowStockProducts ───────────────────────────────────────────────────

    @Test
    void getLowStockProducts_returnsProductsBelowThreshold() {
        sampleProduct.setQuantity(3); // below reorderLevel of 5
        when(productRepository.findByQuantityLessThanEqual(5))
                .thenReturn(List.of(sampleProduct));

        List<Product> result = productDAO.getLowStockProducts(5);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getQuantity() <= 5);
    }

    @Test
    void getLowStockProducts_noneBelow_returnsEmpty() {
        when(productRepository.findByQuantityLessThanEqual(5)).thenReturn(List.of());

        List<Product> result = productDAO.getLowStockProducts(5);

        assertTrue(result.isEmpty());
    }

    // ── updateStock ───────────────────────────────────────────────────────────

    @Test
    void updateStock_existingProduct_savesWithNewQuantity() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        productDAO.updateStock(1L, 75);

        assertEquals(75, sampleProduct.getQuantity());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void updateStock_productNotFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productDAO.updateStock(99L, 10));
        verify(productRepository, never()).save(any());
    }

    // ── deleteProduct ─────────────────────────────────────────────────────────

    @Test
    void deleteProduct_callsRepositoryDeleteById() {
        productDAO.deleteProduct(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    // ── deactivateProduct ─────────────────────────────────────────────────────

    @Test
    void deactivateProduct_setsActiveFalseAndSaves() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        productDAO.deactivateProduct(1L);

        assertFalse(sampleProduct.getActive());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void deactivateProduct_productNotFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productDAO.deactivateProduct(99L));
        verify(productRepository, never()).save(any());
    }
}
