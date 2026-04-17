package com.inventory.service.validation;

import org.springframework.stereotype.Component;

@Component
public class InventoryValidator {

    // Validate quantity input
    public void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
    }

    // Validate stock availability
    public void validateStock(int currentStock, int quantity) {
        if (currentStock < quantity) {
            throw new RuntimeException(
                    "Insufficient stock. Available: " + currentStock + ", Requested: " + quantity
            );
        }
    }


    public void validateProductExists(Object product, int id) {
        if (product == null) {
            throw new RuntimeException("Product with ID " + id + " not found");
        }
    }
}