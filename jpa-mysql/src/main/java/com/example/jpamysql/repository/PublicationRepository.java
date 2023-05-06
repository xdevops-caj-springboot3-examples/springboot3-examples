package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByCategory(String technology);

    @Query("select p from Publication p left join fetch p.articles b where p.category = :category")
    List<Publication> queryByCategory(@Param("category") String category);
}
