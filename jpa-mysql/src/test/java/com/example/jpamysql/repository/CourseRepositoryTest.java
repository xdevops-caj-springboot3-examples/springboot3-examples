package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Course;
import com.example.jpamysql.entity.Student;
import com.example.jpamysql.entity.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void printAllCourses() {
        List<Course> courses = courseRepository.findAll();
        System.out.println("courses = " + courses);
    }

    @Test
    public void testSaveCoursesWithTeacher() {
        Teacher teacher = Teacher.builder()
                .firstName("Lei")
                .lastName("Li")
                .build();

        Course course = Course.builder()
                .title("Python")
                .credit(6)
                .teacher(teacher)
                .build();

        courseRepository.save(course);
    }

    @Test
    public void findAllPagination() {
        Pageable firstPageWithThreeRecords = PageRequest.of(0, 3);
        Page<Course> coursePage = courseRepository.findAll(firstPageWithThreeRecords);
        
        List<Course> courses = coursePage.getContent();
        long totalElements = coursePage.getTotalElements();
        long totalPages = coursePage.getTotalPages();
        
        System.out.println("courses = " + courses);
        System.out.println("totalElements = " + totalElements);
        System.out.println("totalPages = " + totalPages);
    }

    @Test
    public void testFindByOrderByTitleAsc() {
        List<Course> courses = courseRepository.findByOrderByTitleAsc();
        System.out.println("courses = " + courses);
    }

    @Test
    public void testFindByDynamicSort() {
        List<Course> courses = courseRepository.findAll(Sort.by("title").ascending());
        System.out.println("courses = " + courses);
    }

    @Test
    public void testFindByOrderByTitleDesc() {
        List<Course> courses = courseRepository.findByOrderByTitleDesc();
        System.out.println("courses = " + courses);
    }

    @Test
    public void testSortingWithPaging() {
        Pageable sortByTitleAndCredit = PageRequest.of(0, 2,
                Sort.by("title").descending().and(Sort.by("credit").ascending()));
        Page<Course> coursePage = courseRepository.findAll(sortByTitleAndCredit);
        
        List<Course> courses = coursePage.getContent();
        System.out.println("courses = " + courses);
    }

    @Test
    public void testSaveCourseWithTeacherAndStudents() {
        Teacher teacher = Teacher.builder()
                .firstName("Jimmy")
                .lastName("Yang")
                .build();

        Student student1 = Student.builder()
                .firstName("San")
                .lastName("Zhang")
                .emailId("zhangsan@example.com")
                .build();

        Student student2 = Student.builder()
                .firstName("Si")
                .lastName("Li")
                .emailId("lisi@example.com")
                .build();

        Course course = Course.builder()
                .title("AI/ML")
                .credit(12)
                .teacher(teacher)
                .students(List.of(student1, student2))
                .build();

        courseRepository.save(course);
    }

}