package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Guardian;
import com.example.jpamysql.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testSaveStudent() {
        Guardian guardian = Guardian.builder()
                .name("Ye Wen")
                .email("yewen@example.com")
                .mobile("12345678")
                .build();

        Student student = Student.builder()
                .firstName("Bruce")
                .lastName("Lee")
                .emailId("bruce@example.com")
//                .guardianName("Ye Wen")
//                .guardianEmail("yewen@example.com")
//                .guardianMobile("12345678")
                .build();
        studentRepository.save(student);
    }

    @Test
    public void testSaveStudentWithGuardian() {
        Guardian guardian = Guardian.builder()
                .name("Mango")
                .email("mongo@example.com")
                .mobile("888866666")
                .build();

        Student student = Student.builder()
                .firstName("Jacky")
                .lastName("Cheng")
                .emailId("jacky@example.com")
                .guardian(guardian)
                .build();
        studentRepository.save(student);
    }

    @Test
    public void printAllStudents() {
        List<Student> students = studentRepository.findAll();
        System.out.println("students = " + students);
    }
    
    @Test
    public void testFindByFirstName() {
        List<Student> students = studentRepository.findByFirstName("Bruce");
        System.out.println("students = " + students);
    }

    @Test
    public void testFindByFirstNameContaining() {
        List<Student> students = studentRepository.findByFirstNameContaining("ruce");
        System.out.println("students = " + students);
    }

    @Test
    public void testFindByLastNameNotNull() {
        List<Student> students = studentRepository.findByLastNameNotNull();
        System.out.println("students = " + students);
    }
    
    @Test
    public void testGetStudentByEmailAddress() {
        Student student = studentRepository.getStudentByEmailAddress("bruce@example.com");
        System.out.println("student = " + student);
    }

    @Test
    public void testGetStudentFirstNameByEmailAddress() {
        String firstName = studentRepository.getStudentFirstNameByEmailAddress("bruce@example.com");
        System.out.println("firstName = " + firstName);
    }

    @Test
    public void testGetStudentByEmailAddressNative() {
        Student student = studentRepository.getStudentByEmailAddressNative("bruce@example.com");
        System.out.println("student = " + student);
    }

    @Test
    public void testGetStudentFirstNameByEmailAddressNative() {
        String firstName = studentRepository.getStudentFirstNameByEmailAddressNative("bruce@example.com");
        System.out.println("firstName = " + firstName);
    }

    @Test
    public void testUpdateStudentNameByEmailId() {
        int state = studentRepository.updateStudentNameByEmailId("Bruce", "Li", "bruce@example.com");
        assertEquals(1, state);
    }

    @Test
    public void testFindStudentsByFirstNames() {
//        studentRepository.save(Student.builder()
//                .firstName("William")
//                .lastName("Lin")
//                .emailId("william@example.com")
//                .build());
//        studentRepository.save(Student.builder()
//                .firstName("John")
//                .lastName("Huang")
//                .emailId("john@example.com")
//                .build());

        List<String> firstNames = List.of("William", "Tom", "John", "Jacky", "Frog");
        String lastName = "Lin";
        List<Student> students = studentRepository.findStudentsByNames(firstNames, lastName, PageRequest.of(0, 10));
        System.out.println("students = " + students);
    }
}