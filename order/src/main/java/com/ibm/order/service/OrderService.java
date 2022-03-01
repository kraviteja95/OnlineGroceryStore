package com.ibm.order.service;

import com.ibm.order.model.Order;
import com.ibm.order.model.Product;
import com.ibm.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public Order addOrder(Order order) {
        List<Product> productList = order.getProducts();
        Order orderRes = orderRepository.save(order);
        productList.forEach(i -> i.setOrderId(orderRes.getOrderId()));
        orderRes.setProducts(productList);
        return orderRepository.save(orderRes);
    }
}
