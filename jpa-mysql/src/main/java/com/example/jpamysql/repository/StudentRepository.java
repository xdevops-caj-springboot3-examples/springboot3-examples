package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByFirstName(String firstName);

    List<Student> findByFirstNameContaining(String name);

    List<Student> findByLastNameNotNull();

    @Query("select s from Student s where s.emailId = ?1")
    Student getStudentByEmailAddress(String emailAddress);

    @Query("select s.firstName from Student s where s.emailId = :email")
    String getStudentFirstNameByEmailAddress(@Param("email") String emailAddress);

    @Query(
            value = "select s.* from t_student s where s.email_address = ?1",
            nativeQuery = true
    )
    Student getStudentByEmailAddressNative(String emailAddress);

    @Query(
            value = "select s.first_name from t_student s where s.email_address = :email",
            nativeQuery = true
    )
    String getStudentFirstNameByEmailAddressNative(@Param("email") String emailAddress);

    @Modifying
    @Transactional
    @Query("update Student set firstName = :firstName, lastName = :lastName where emailId = :email")
    int updateStudentNameByEmailId(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String emailId);
}
