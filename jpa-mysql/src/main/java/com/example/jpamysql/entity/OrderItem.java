package com.example.jpamysql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_order_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
