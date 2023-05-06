package com.example.jpamysql.repository;

import com.example.jpamysql.entity.view.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Long> {

    List<OrderSummary> findByOrderId(Long orderId);
}
