package com.example.asyncgeneratepdf.repository;

import com.example.asyncgeneratepdf.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
