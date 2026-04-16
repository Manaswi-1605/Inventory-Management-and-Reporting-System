package com.inventory.database_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String contactEmail;

    @Column(length = 20)
    private String phone;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Supplier() {}

    public Supplier(Long id, String name, String contactEmail, String phone) {
        this.id           = id;
        this.name         = name;
        this.contactEmail = contactEmail;
        this.phone        = phone;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId()             { return id; }
    public String getName()         { return name; }
    public String getContactEmail() { return contactEmail; }
    public String getPhone()        { return phone; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setId(Long id)                     { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setPhone(String phone)             { this.phone = phone; }
}