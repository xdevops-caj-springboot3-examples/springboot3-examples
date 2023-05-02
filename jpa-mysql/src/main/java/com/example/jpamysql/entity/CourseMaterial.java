package com.example.jpamysql.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_course_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"course"})
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseMaterialId;
    private String url;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "course_id",
            referencedColumnName = "courseId"
    )
    private Course course;
}
