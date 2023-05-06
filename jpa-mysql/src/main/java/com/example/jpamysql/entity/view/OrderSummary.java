package com.example.jpamysql.entity.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "v_order_summary")
@Getter
public class OrderSummary {

    @Id
    private Long itemId;
    private Long orderId;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "product_name")
    private String productName;
    private Integer quantity;
}
