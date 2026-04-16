package com.inventory.dao;

import com.inventory.database_system.entity.Product;
import com.inventory.database_system.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAO {

    private final ProductRepository productRepository;

    public ProductDAO(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Replaces old: getProductById(int id)
     * Now returns Optional — callers must handle not-found case.
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsBySupplier(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Replaces old raw SQL low-stock query.
     * Fetches products whose quantity <= their own reorderLevel.
     * Pass product.getReorderLevel() or a fixed threshold.
     */
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByQuantityLessThanEqual(threshold);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /**
     * Replaces old: updateStock(int id, int stock)
     * Now operates on the `quantity` field (renamed from `stock`).
     */
    public void updateStock(Long id, int quantity) {
        Product product = getProductById(id);
        product.setQuantity(quantity);
        productRepository.save(product);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Soft delete — marks product inactive instead of removing from DB.
     * Preferred over deleteProduct() to preserve transaction history.
     */
    public void deactivateProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}
