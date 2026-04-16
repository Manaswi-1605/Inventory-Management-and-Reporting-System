package com.inventory.database_system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String type; // "INCREASE" or "REDUCE"

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Transaction() {}

    public Transaction(Long id, Product product, String type, int quantity, LocalDateTime createdAt) {
        this.id        = id;
        this.product   = product;
        this.type      = type;
        this.quantity  = quantity;
        this.createdAt = createdAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()                 { return id; }
    public Product getProduct()         { return product; }
    public String getType()             { return type; }
    public int getQuantity()            { return quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(Long id)                       { this.id = id; }
    public void setProduct(Product product)          { this.product = product; }
    public void setType(String type)                 { this.type = type; }
    public void setQuantity(int quantity)            { this.quantity = quantity; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ── JPA lifecycle ─────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}