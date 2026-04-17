package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.inventory", "com.database"})
@EntityScan(basePackages = {
        "com.inventory.database_system.entity",   // Product, Category, Supplier, Transaction
        "com.inventory.entity"                    // User
})
@EnableJpaRepositories(basePackages = {
        "com.inventory.database_system.repository",  // ProductRepository
        "com.inventory.repository"                   // UserRepository
})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
