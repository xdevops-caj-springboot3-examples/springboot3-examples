package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select a from Author a left join fetch a.books b where a.authorId = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long authorId);

    Optional<Author> findByName(String name);

    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {"books"}
    )
    List<Author> findByNationality(String nationality);
}
