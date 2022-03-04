package com.ibm.order.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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