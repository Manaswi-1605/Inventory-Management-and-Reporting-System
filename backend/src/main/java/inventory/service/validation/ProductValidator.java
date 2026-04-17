package com.inventory.service.validation;

import com.inventory.database_system.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductValidator {

    public void validate(Product product) {

        if (product.getName() == null || product.getName().isBlank()) {
            throw new RuntimeException("Product name required");
        }

        if (product.getSku() == null || product.getSku().isBlank()) {
            throw new RuntimeException("Product SKU required");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid price");
        }

        if (product.getQuantity() < 0) {
            throw new RuntimeException("Invalid quantity");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new RuntimeException("Category is required");
        }

        if (product.getSupplier() == null || product.getSupplier().getId() == null) {
            throw new RuntimeException("Supplier is required");
        }
    }
}
