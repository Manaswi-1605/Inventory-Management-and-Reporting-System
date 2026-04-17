package com.inventory;

import com.inventory.Report.InventoryReportService;
import com.inventory.dao.ProductDAO;
import com.inventory.database_system.entity.Product;
import com.inventory.service.InventoryService;
import com.inventory.service.validation.InventoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductDAO productDAO;

    @Mock
    private InventoryValidator validator;

    @Mock
    private InventoryReportService reportService;

    @InjectMocks
    private InventoryService inventoryService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Keyboard");
        sampleProduct.setSku("KB-001");
        sampleProduct.setPrice(new BigDecimal("49.99"));
        sampleProduct.setQuantity(50);
        sampleProduct.setReorderLevel(10);
        sampleProduct.setActive(true);
    }

    // ── reduceStock ───────────────────────────────────────────────────────────

    @Test
    void reduceStock_valid_updatesQuantityCorrectly() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        inventoryService.reduceStock(1L, 20);

        // new quantity should be 50 - 20 = 30
        verify(productDAO).updateStock(1L, 30);
    }

    @Test
    void reduceStock_valid_triggersLowStockReport() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        inventoryService.reduceStock(1L, 5);

        verify(reportService, times(1)).checkAndSendLowStockAlert();
    }

    @Test
    void reduceStock_invalidQuantity_throwsBeforeFetchingProduct() {
        doThrow(new RuntimeException("Quantity must be greater than 0"))
                .when(validator).validateQuantity(0);

        assertThrows(RuntimeException.class, () -> inventoryService.reduceStock(1L, 0));

        // Should never reach the DB call
        verify(productDAO, never()).getProductById(any());
        verify(productDAO, never()).updateStock(any(), anyInt());
    }

    @Test
    void reduceStock_productNotFound_throws() {
        when(productDAO.getProductById(99L))
                .thenThrow(new RuntimeException("Product not found with id: 99"));

        assertThrows(RuntimeException.class, () -> inventoryService.reduceStock(99L, 5));
        verify(productDAO, never()).updateStock(any(), anyInt());
    }

    @Test
    void reduceStock_insufficientStock_throws() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);
        doThrow(new RuntimeException("Insufficient stock. Available: 50, Requested: 100"))
                .when(validator).validateStock(50, 100);

        assertThrows(RuntimeException.class, () -> inventoryService.reduceStock(1L, 100));
        verify(productDAO, never()).updateStock(any(), anyInt());
    }

    // ── increaseStock ─────────────────────────────────────────────────────────

    @Test
    void increaseStock_valid_updatesQuantityCorrectly() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        inventoryService.increaseStock(1L, 30);

        // new quantity should be 50 + 30 = 80
        verify(productDAO).updateStock(1L, 80);
    }

    @Test
    void increaseStock_valid_doesNotTriggerReport() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        inventoryService.increaseStock(1L, 10);

        // report is only triggered on reduce, not increase
        verify(reportService, never()).checkAndSendLowStockAlert();
    }

    @Test
    void increaseStock_zeroQuantity_throwsBeforeFetchingProduct() {
        doThrow(new RuntimeException("Quantity must be greater than 0"))
                .when(validator).validateQuantity(0);

        assertThrows(RuntimeException.class, () -> inventoryService.increaseStock(1L, 0));

        verify(productDAO, never()).getProductById(any());
        verify(productDAO, never()).updateStock(any(), anyInt());
    }

    @Test
    void increaseStock_productNotFound_throws() {
        when(productDAO.getProductById(55L))
                .thenThrow(new RuntimeException("Product not found with id: 55"));

        assertThrows(RuntimeException.class, () -> inventoryService.increaseStock(55L, 10));
    }

    // ── getStock ──────────────────────────────────────────────────────────────

    @Test
    void getStock_existingProduct_returnsQuantity() {
        when(productDAO.getProductById(1L)).thenReturn(sampleProduct);

        int stock = inventoryService.getStock(1L);

        assertEquals(50, stock);
    }

    @Test
    void getStock_productNotFound_throws() {
        when(productDAO.getProductById(99L))
                .thenThrow(new RuntimeException("Product not found with id: 99"));

        assertThrows(RuntimeException.class, () -> inventoryService.getStock(99L));
    }
}

