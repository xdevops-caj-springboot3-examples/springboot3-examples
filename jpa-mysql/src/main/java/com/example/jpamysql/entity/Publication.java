package com.example.jpamysql.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_publication")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long publicationId;
    private String name;
    private String category;

    @OneToMany(
            mappedBy = "publication",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Article> articles = new ArrayList<>();
}
