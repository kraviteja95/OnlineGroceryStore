package com.ibm.login.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "order_details")
@Data
public class Order {
    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(name = "id")

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
        CascadeType.REFRESH })

    private List<Product> products;

    @Column(name = "orderDate")
    private LocalDateTime orderDate;

    private String txnToken;
    private String userToken;
    private String serviceToken;

    public void add(Product product) {
        if (products == null) {
            products = new ArrayList<>();
        }
        products.add(product);
        product.setOrder(this);
    }
}