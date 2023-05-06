package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Customer;
import com.example.jpamysql.entity.Order;
import com.example.jpamysql.entity.OrderItem;
import com.example.jpamysql.entity.Product;
import com.example.jpamysql.entity.view.OrderSummary;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j
class OrderSummaryRepositoryTest {

    @Autowired
    private OrderSummaryRepository orderSummaryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testSaveOrder() {
        Customer customer = Customer.builder()
                .name("John Doe")
                .build();
        customerRepository.save(customer);

        Product macBookPro = Product.builder()
                .productName("MacBook Pro")
                .build();
        productRepository.save(macBookPro);

        Product clock = Product.builder()
                .productName("Clock")
                .build();
        productRepository.save(clock);

        Order order = Order.builder()
                .orderDate(java.time.LocalDate.now())
                .customerId(customer.getCustomerId())
                .build();
        orderRepository.save(order);

        OrderItem item1 = OrderItem.builder()
                .orderId(order.getOrderId())
                .productId(macBookPro.getProductId())
                .quantity(1)
                .build();
        OrderItem item2 = OrderItem.builder()
                .orderId(order.getOrderId())
                .productId(clock.getProductId())
                .quantity(2)
                .build();
        orderItemRepository.save(item1);
        orderItemRepository.save(item2);
    }

    @Test
    public void testFindByOrderId() {
        List<OrderSummary> orderSummaryList = orderSummaryRepository
                .findByOrderId(1L);
        assertEquals(2, orderSummaryList.size());
        for(OrderSummary orderSummary : orderSummaryList) {
            log.info("orderId={}, customerName={}, orderDate={}, productName={}, quantity={}",
                    orderSummary.getOrderId(),
                    orderSummary.getCustomerName(),
                    orderSummary.getOrderDate(),
                    orderSummary.getProductName(),
                    orderSummary.getQuantity());
        }
    }

}