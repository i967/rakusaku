package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList; // 1. Import ArrayList
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType; // 2. Import CascadeType
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_user_id", unique = true, nullable = false)
    private String lineUserId;

    @Column(name = "display_name")
    private String displayName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 3. Improve the relationship mapping and initialize the list
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopOrder> orders = new ArrayList<>();

    // 4. Add the helper method to safely add an order
    public void addOrder(ShopOrder order) {
        this.orders.add(order);
        order.setCustomer(this);
    }
}