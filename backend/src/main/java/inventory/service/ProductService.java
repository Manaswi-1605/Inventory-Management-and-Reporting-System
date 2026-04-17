package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.database_system.entity.Product;
import com.inventory.service.validation.ProductValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductDAO productDAO;
    private final ProductValidator validator;

    public ProductService(ProductDAO productDAO, ProductValidator validator) {
        this.productDAO = productDAO;
        this.validator = validator;
    }

    public void addProduct(Product product) {
        validator.validate(product);
        productDAO.addProduct(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> getActiveProducts() {
        return productDAO.getActiveProducts();
    }

    // id type changed: int → Long
    public Product getProduct(Long id) {
        return productDAO.getProductById(id);
    }

    public List<Product> searchByName(String name) {
        return productDAO.searchByName(name);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productDAO.getProductsByCategory(categoryId);
    }

    public List<Product> getProductsBySupplier(Long supplierId) {
        return productDAO.getProductsBySupplier(supplierId);
    }

    // id type changed: int → Long
    public void deleteProduct(Long id) {
        productDAO.deleteProduct(id);
    }

    public void deactivateProduct(Long id) {
        productDAO.deactivateProduct(id);
    }
}
