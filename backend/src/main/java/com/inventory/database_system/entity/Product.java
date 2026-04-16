package com.inventory.database_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_supplier", columnList = "supplier_id")
})
//@JsonIgnoreProperties({"category", "supplier", "transactions"})
@JsonIgnoreProperties(value = {"category", "supplier", "transactions"}, allowSetters = true)

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Integer reorderLevel = 10;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    // ── Constructors ──────────────────────────────────────────────────────────

    public Product() {}

    public Product(Long id, String name, String description, String sku,
                   BigDecimal price, int quantity, Integer reorderLevel,
                   Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt,
                   Category category, Supplier supplier, List<Transaction> transactions) {
        this.id           = id;
        this.name         = name;
        this.description  = description;
        this.sku          = sku;
        this.price        = price;
        this.quantity     = quantity;
        this.reorderLevel = reorderLevel;
        this.active       = active;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
        this.category     = category;
        this.supplier     = supplier;
        this.transactions = transactions;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()                        { return id; }
    public String getName()                    { return name; }
    public String getDescription()             { return description; }
    public String getSku()                     { return sku; }
    public BigDecimal getPrice()               { return price; }
    public int getQuantity()                   { return quantity; }
    public Integer getReorderLevel()           { return reorderLevel; }
    public Boolean getActive()                 { return active; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public Category getCategory()              { return category; }
    public Supplier getSupplier()              { return supplier; }
    public List<Transaction> getTransactions() { return transactions; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(Long id)                               { this.id = id; }
    public void setName(String name)                         { this.name = name; }
    public void setDescription(String description)           { this.description = description; }
    public void setSku(String sku)                           { this.sku = sku; }
    public void setPrice(BigDecimal price)                   { this.price = price; }
    public void setQuantity(int quantity)                    { this.quantity = quantity; }
    public void setReorderLevel(Integer reorderLevel)        { this.reorderLevel = reorderLevel; }
    public void setActive(Boolean active)                    { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt)        { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)        { this.updatedAt = updatedAt; }
    public void setCategory(Category category)               { this.category = category; }
    public void setSupplier(Supplier supplier)               { this.supplier = supplier; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    // ── JPA lifecycle ─────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Business logic ────────────────────────────────────────────────────────

    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }
}