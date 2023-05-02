package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByOrderByTitleAsc();

    List<Course> findByOrderByTitleDesc();

}
