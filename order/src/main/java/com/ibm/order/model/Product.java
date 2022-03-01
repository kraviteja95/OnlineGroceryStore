package com.ibm.order.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product_details")
@Data
public class Product {
    @Id
    @Column(name = "productId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @Column(name = "productName")
    private String productName;

    @Column(name = "productQuantity")
    private String productQuantity;

    @Column(name = "productPrice")
    private double productPrice;

    @Column(name = "orderId")
    private int orderId;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })

    @JoinColumn(name = "id")
    private Order order;
}