package com.inventory.Report;

import com.inventory.dao.ProductDAO;
import com.inventory.database_system.entity.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryReportService {

    private final ProductDAO productDAO;

    public InventoryReportService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void checkAndSendLowStockAlert() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<String> lowStockLines = new ArrayList<>();

            for (Product product : allProducts) {
                System.out.println(
                        product.getId() + "|" +
                        product.getName() + "|" +
                        product.getQuantity() + "|" +            // was: getStock()
                        product.getPrice() + "|" +
                        product.getReorderLevel()                // was: getMinQuantity()
                );

                // isLowStock() checks quantity <= reorderLevel
                if (product.isLowStock()) {                      // was: getStock() < getMinQuantity()
                    lowStockLines.add(
                            String.format(" - %s (ID: %d) | Category: %s | Quantity: %d | Reorder Level: %d | Price: %.2f",
                                    product.getName(),
                                    product.getId(),
                                    product.getCategory().getName(),  // was: getCategory() (String)
                                    product.getQuantity(),             // was: getStock()
                                    product.getReorderLevel(),         // was: getMinQuantity()
                                    product.getPrice().doubleValue())  // was: getPrice() (double)
                    );
                }
            }

            if (!lowStockLines.isEmpty()) {
                sendLowStockAlert(lowStockLines);
            } else {
                System.out.println("All products are sufficiently stocked.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLowStockAlert(List<String> lowStockLines) {
        EmailService emailService = new EmailService();

        String subject = "LOW STOCK ALERT — Action Required";

        StringBuilder body = new StringBuilder();
        body.append("Hello,\n\n");
        body.append("The following products have dropped below their reorder level:\n\n");
        for (String line : lowStockLines) {
            body.append(line).append("\n");
        }
        body.append("\nPlease restock these items as soon as possible.\n\n");
        body.append("-- Inventory System");

        emailService.sendEmail(
                "",   // ← Set receiver email here
                subject,
                body.toString()
        );

        System.out.println("Low stock alert sent for " + lowStockLines.size() + " product(s).");
    }
}
