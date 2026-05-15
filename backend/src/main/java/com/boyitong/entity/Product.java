package com.boyitong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String unit;
    private Double price;
    private String description;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; } public void setUnit(String unit) { this.unit = unit; }
    public Double getPrice() { return price; } public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
}