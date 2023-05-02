package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Course;
import com.example.jpamysql.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    public void testSaveTeacher() {
        Teacher teacher = Teacher.builder()
                .firstName("Gong")
                .lastName("Wu")
                .build();

        teacherRepository.save(teacher);
    }

//    @Test
//    public void testSaveTeacherWithCourses() {
//        Course courseJava = Course.builder()
//                .title("Java")
//                .credit(6)
//                .build();
//
//        Course courseVueJS = Course.builder()
//                .title("VueJS")
//                .credit(6)
//                .build();
//
//        Teacher teacher = Teacher.builder()
//                .firstName("Feihong")
//                .lastName("Huang")
//                .courses(List.of(courseJava, courseVueJS))
//                .build();
//
//        teacherRepository.save(teacher);
//    }
}