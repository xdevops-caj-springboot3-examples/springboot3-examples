package com.example.jpamysql.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_article")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;
    private String title;

    @ManyToOne(
            optional = false
    )
    @JoinColumn(
            name = "publication_id",
            referencedColumnName = "publicationId",
            nullable = false
    )
    private Publication publication;
}
