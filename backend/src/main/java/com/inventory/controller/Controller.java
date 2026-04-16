package com.inventory.controller;

import com.inventory.database_system.entity.Product;
import com.inventory.service.ProductService;
import com.inventory.service.InventoryService;
import com.inventory.Report.InventoryReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final InventoryReportService reportService;

    public Controller(ProductService productService,
                      InventoryService inventoryService,
                      InventoryReportService reportService) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.reportService = reportService;
    }

    // ──────────────────────── PRODUCT ────────────────────────────────────────

    /**
     * POST /api/product
     *
     * Body example:
     * {
     *   "name":        "Wireless Mouse",
     *   "sku":         "WM-001",
     *   "description": "Ergonomic wireless mouse",
     *   "price":       29.99,
     *   "quantity":    100,
     *   "reorderLevel": 15,
     *   "category":    { "id": 1 },
     *   "supplier":    { "id": 2 }
     * }
     */
    @PostMapping("/product")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added");
    }

    /**
     * GET /api/products
     * Returns ALL products including inactive ones.
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * GET /api/products/active
     * Returns only active products (active = true).
     */
    @GetMapping("/products/active")
    public ResponseEntity<List<Product>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    /**
     * GET /api/product/{id}
     * Changed path variable type from int → Long to match new entity id type.
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    /**
     * GET /api/products/search?name=mouse
     * Search products by name (case-insensitive, partial match).
     */
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    /**
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    /**
     * GET /api/products/supplier/{supplierId}
     */
    @GetMapping("/products/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(productService.getProductsBySupplier(supplierId));
    }

    /**
     * DELETE /api/product/{id}
     * Hard delete. Use /api/product/{id}/deactivate for soft delete.
     * Changed path variable type from int → Long.
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted");
    }

    /**
     * PATCH /api/product/{id}/deactivate
     * Soft delete — sets active = false, preserves transaction history.
     */
    @PatchMapping("/product/{id}/deactivate")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok("Product deactivated");
    }

    // ──────────────────────── INVENTORY ──────────────────────────────────────

    /**
     * POST /api/reduce/{id}/{qty}
     * Changed path variable type from int → Long for id.
     */
    @PostMapping("/reduce/{id}/{qty}")
    public ResponseEntity<String> reduceStock(@PathVariable Long id,
                                              @PathVariable int qty) {
        inventoryService.reduceStock(id, qty);
        return ResponseEntity.ok("Stock reduced");
    }

    /**
     * POST /api/increase/{id}/{qty}
     * Changed path variable type from int → Long for id.
     */
    @PostMapping("/increase/{id}/{qty}")
    public ResponseEntity<String> increaseStock(@PathVariable Long id,
                                                @PathVariable int qty) {
        inventoryService.increaseStock(id, qty);
        return ResponseEntity.ok("Stock increased");
    }

    // ──────────────────────── REPORT ─────────────────────────────────────────

    /**
     * GET /api/report
     * Triggers low-stock check and sends email alert if needed.
     */
    @GetMapping("/report")
    public ResponseEntity<String> generateReport() {
        System.out.println("Controller: Generating report...");
        reportService.checkAndSendLowStockAlert();
        return ResponseEntity.ok("Report generated - check your email");
    }
}
