# Spring Data JPA with MySQL

## References

- [Spring Data JPA Tutorial | Full In-depth Course](https://www.youtube.com/watch?v=XszpXoII9Sg)
- https://github.com/shabbirdwd53/Spring-Data-JPA-Tutorial
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

## Lab env

### Tools

- Database: Running MySQL with container
- Database client: MySQL Workbench


### Running MySQL with container

```bash
podman run --rm --name local-mysql \
  -e MYSQL_ROOT_PASSWORD=admin123 \
  -p 3306:3306 \
  -d mysql
```

Verify container running:
```bash
podman ps
```

### Connect MySQL database with MySQL Workbench

Database / Manage connections.

New, and input as below:

```text
Connection Name: local-mysql
Hostname: 127.0.0.1
Port: 3306
Username: root
Password: <MYSQL_ROOT_PASSWORD>
```

Database / Connect to Database, choose stored connection as `local-mysql`.

### Create a database schema

After connected to the `local-mysql`, create a new database schema.

```text
Schema Name: schooldb
Character Set: utf8mb4
Collation: utf8mb4_unicode_ci
```

Or execute SQL:
```sql
CREATE SCHEMA `schooldb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
```

Switch to the new created schema:

```bash
use schooldb;
```

## Connecting SpringBoot App with MySQL

The application.yaml as below:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/schooldb?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: admin123
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update # Don't enable ddl-auto for production
    show-sql: true
```

Notes:
- `spring.jpa.hibernate.ddl-auto`: The DDL mode that Hibernate should use when creating or updating the database schema. 
  Set this to `update` to update the schema automatically based on your entity classes.
- `spring.jpa.show-sql`: Whether to enable SQL logging. Set this to true to log SQL statements to the console.

In production, you should disable `ddl-auto` by:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none
```
The default value of `ddl-auto` is `none`.

## Mapping Entities with Database

Create a `Student` entity class:

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    private Long studentId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String guardianName;
    private String guardianEmail;
    private String guardianMobile;
}
```

Notes:
- The `@Entity` annotation, which tells JPA that instances of this class should be persisted to a database table.
- The `@Id` annotation is used to mark the id field as the primary key of the table.
- Other fields are mapped to columns in the table.

Run the application:
```bash
mvn spring-boot:run
```

The `student` table will be created automatically in the database.

Check logs:
```sql
 create table student (student_id bigint not null, email_id varchar(255), first_name varchar(255), guardian_email varchar(255), guardian_mobile varchar(255), guardian_name varchar(255), last_name varchar(255), primary key (student_id)) engine=InnoDB
```

Check the table by the MySQL Workbench.

## Different JPA Annotations

### Specify the table name

```java
@Entity
@Table(name = "t_student")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    // ...
}
```


Notes:
- The `@Table` annotation is used to specify that instances of the Customer class should be stored in a database table named `t_student`.

### Specify the column name and its attributes

```java
    @Column(name = "email_address", nullable = false)
    private String emailId;
```

Notes:
- Using the `@Column` annotation, you can map a Java object's properties to the corresponding columns in a database table, and customize the mapping with various attributes.
- The column is not null

### Generating ID

In Spring Data JPA, the @GeneratedValue annotation can be used with different strategies to generate primary key values for entities. 
Two commonly used strategies are IDENTITY and SEQUENCE.

IDENTITY strategy example:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long sutdentId;
```

SEQUENCY strategy example:
```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
@SequenceGenerator(name = "student_seq", sequenceName = "student_seq", allocationSize = 1)
private Long studentId;
```

The choice between IDENTITY and SEQUENCE strategies depends on the capabilities of your database and the requirements of your application. 

Generally, IDENTITY is simpler to use, but SEQUENCE provides more flexibility in certain scenarios, such as when you need to generate primary key values in batches or across multiple transactions.

### Add unique constraints

The `email_address` in the table is unique.

```java
    @Column(name = "email_address", unique = true)
    private String emailId;
```

Or use the `@Table` annotation with the `uniqueConstraints` attribute. 
This allows you to specify a unique constraint across multiple columns.

```java
@Table(name = "t_student",
        uniqueConstraints = {
                @UniqueConstraint(name = "email_address_unique", columnNames = {"email_address"})
        })
```

### Re-run the application

```bash
mvn spring-boot:run
```

Check the logs:

```sql
create table t_student (student_id bigint not null auto_increment, email_address varchar(255) not null, first_name varchar(255), guardian_email varchar(255), guardian_mobile varchar(255), guardian_name varchar(255), last_name varchar(255), primary key (student_id)) engine=InnoDB
alter table t_student drop index email_address_unique
alter table t_student add constraint email_address_unique unique (email_address)
```

Notes:
- Create a `t_student` table
- Create the `email_address_unique` unique index with `email_address` column.

In MySQL Workbench, find the new created `t_sutdent` table, right click and choose Table Inpsector to check the table details.

Drop the previous created `student` table.

## Understanding Repositories and their methods

Create `StudentRepository` repository interface:

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

Notes:
- The `@Repository` annotation is optional. However, it's a good practice to include the `@Repository` annotation on your repository classes as it clearly indicates the intended purpose of the class and can make your code more readable and maintainable. 
- Use `JpaRepository` for more advanced query functionality or paging/sorting support besides CRUD operations.

### Test Repository

Create the test class of `StudentRepository` as `StudentRepositoryTest` test class.

```java
@SpringBootTest

class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void testSaveStudent() {
        Student student = Student.builder()
                .firstName("Bruce")
                .lastName("Lee")
                .emailId("bruce@example.com")
                .guardianName("Ye Wen")
                .guardianEmail("yewen@example.com")
                .guardianMobile("12345678")
                .build();
        studentRepository.save(student);
    }
}
```

Notes:
- You can use `@DataJpaTest` and an embedded database for unit test of repository layer. This example use `@SpringBootTest` directly and view data changed in MySQL database.
- Autowire the repository you want to test in your test class.
- Use `@Builder` for `Student` class for builder pattern

Run the test method.

Verify the data changed in MySQL workbench:
```sql
select * from t_student
```

Check logs:
```sql
insert into t_student (email_address, first_name, guardian_email, guardian_mobile, guardian_name, last_name) values (?, ?, ?, ?, ?, ?)
```

### Test print all students

```java
    @Test
    public void printAllStudents() {
        List<Student> students = studentRepository.findAll();
        System.out.println("students = " + students);
    }
```

Run the test method.

Check logs:
```sql
select s1_0.student_id,s1_0.email_address,s1_0.first_name,s1_0.guardian_email,s1_0.guardian_mobile,s1_0.guardian_name,s1_0.last_name from t_student s1_0
```

The print result:
```text
students = [Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardianName=Ye Wen, guardianEmail=yewen@example.com, guardianMobile=12345678)]
```

## @Embeddable and @Embedded

Define a `@Embeddable` class:

```java
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "guardian_name")),
        @AttributeOverride(name = "email", column = @Column(name = "guardian_email")),
        @AttributeOverride(name = "mobile", column = @Column(name = "guardian_mobile"))
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guardian {
    private String name;
    private String email;
    private String mobile;
}
```

Notes:
- The `@Embeddable` annotation is used to mark a class as an embeddable class. An embeddable class is a value type that is used as a component of an entity, rather than an entity itself. An embeddable class is mapped to one or more columns in the database table that corresponds to the entity.
- The `@AttributeOverrides` annotation is used to specify multiple `@AttributeOverride` annotations that customize the column names and attributes of the embedded object.

Use the `@Embedded` class:

```java
@Entity
@Table(name = "t_student",
    uniqueConstraints = {
        @UniqueConstraint(name = "email_address_unique", columnNames = {"email_address"})
    })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String firstName;
    private String lastName;

    @Column(name = "email_address", nullable = false)
    private String emailId;

    @Embedded
    private Guardian guardian;
}
```

Notes:
- The `@Embedded` annotation is used to specify that an embeddable class should be embedded into the owning entity.

### Test the embedded class

Comment the `testSaveStudent()` method.

Create a new test method:

```java
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
```

Run the test method.

Check the data changed in the database.

Check logs:

```sql
insert into t_student (email_address, first_name, guardian_email, guardian_mobile, guardian_name, last_name) values (?, ?, ?, ?, ?, ?)
```

## JPA Repositories Methods

Query methods:
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.predicate

### Find By

Define query methods in `StudentRepository` interfaces:

```java
List<Student> findByFirstName(String firstName);
```

Test the query method:

```java
    @Test
    public void testFindByFirstName() {
        List<Student> students = studentRepository.findByFirstName("Bruce");
        System.out.println("students = " + students);
    }
```

Run the test method.

The query SQL logs:
```sql
select s1_0.student_id,s1_0.email_address,s1_0.first_name,s1_0.guardian_email,s1_0.guardian_mobile,s1_0.guardian_name,s1_0.last_name from t_student s1_0 where s1_0.first_name=?
```

Notes:
- The where cause is `where s1_0.first_name=?`

The query result:

```text
[Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardian=Guardian(name=Ye Wen, email=yewen@example.com, mobile=12345678))]
```

### Containing

Define query methods in `StudentRepository` interfaces:

```java
List<Student> findByFirstNameContaining(String name);
```

Test the query method:

```java
@Test
public void testFindByFirstNameContaining() {
        List<Student> students = studentRepository.findByFirstNameContaining("ruce");
        System.out.println("students = " + students);
        }
```

Runt the test method.

The query SQL logs:
```sql
select s1_0.student_id,s1_0.email_address,s1_0.first_name,s1_0.guardian_email,s1_0.guardian_mobile,s1_0.guardian_name,s1_0.last_name from t_student s1_0 where s1_0.first_name like ? escape '\\'
```

Notes:
- The where cause is `where s1_0.first_name like ?`

The query result:

```text
students = [Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardian=Guardian(name=Ye Wen, email=yewen@example.com, mobile=12345678))]
```

### Not Null

Define query methods in `StudentRepository` interfaces:

```java
List<Student> findByLastNameNotNull();
```

Test the query method:

```java
@Test
public void testFindByLastNameNotNull() {
        List<Student> students = studentRepository.findByLastNameNotNull();
        System.out.println("students = " + students);
        }
```

Runt the test method.

The query SQL logs:
```sql
select s1_0.student_id,s1_0.email_address,s1_0.first_name,s1_0.guardian_email,s1_0.guardian_mobile,s1_0.guardian_name,s1_0.last_name from t_student s1_0 where s1_0.last_name is not null
```

Notes:
- The where cause is `where s1_0.last_name is not null`

The query result:

```text
students = [Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardian=Guardian(name=Ye Wen, email=yewen@example.com, mobile=12345678)), Student(studentId=3, firstName=Jacky, lastName=Cheng, emailId=jacky@example.com, guardian=Guardian(name=Mango, email=mongo@example.com, mobile=888866666))]
```

## JPA @Query annotation

References:
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.at-query

The `@Query` annotation in Spring Data JPA is used to define custom JPQL (Java Persistence Query Language) or native SQL queries in your repository methods.

### Get a record

Add a customized query method in the `StudentRepository` class.

```java
    @Query("select s from Student s where s.emailId = ?1")
    Student getStudentByEmailAddress(String emailAddress);
```

Notes:
- The query is JPQL syntax.
- `Student` is for `Student` entity class name not table name
- `emailId` is for field name of `Student` entity class name not column name
- `?1` is the first parameter
- It is `select s` not `select s.*`

Create the related test method:

```java
    @Test
    public void testGetStudentByEmailAddress() {
        Student student = studentRepository.getStudentByEmailAddress("bruce@example.com");
        System.out.println("student = " + student);
    }
```

Run the test method.

The query SQL logs:

```sql
select s1_0.student_id,s1_0.email_address,s1_0.first_name,s1_0.guardian_email,s1_0.guardian_mobile,s1_0.guardian_name,s1_0.last_name 
from t_student s1_0 where s1_0.email_address=?
```

Notes:
- The where cause is `where s1_0.email_address=?`

The query result:

```text
Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardian=Guardian(name=Ye Wen, email=yewen@example.com, mobile=12345678))
```

### Get a field of record

Query named parameters references:
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.named-parameters

Add a customized query method in the `StudentRepository` class.

```java
    @Query("select s.firstName from Student s where s.emailId = :email")
    String getStudentFirstNameByEmailAddress(@Param("email") String emailAddress);
```

Notes:
- The query is JPQL syntax.
- `Student` is for `Student` entity class name not table name
- `emailId` is for field name of `Student` entity class name not column name
- Use `@Param("email")` to define a named query parameter, and use `:email` to refer the parameter in JPQL
- It is `select s.firstName` not `select s.first_name`

Create the related test method:

```java
@Test
public void testGetStudentFirstNameByEmailAddress() {
    String firstName = studentRepository.getStudentFirstNameByEmailAddress("bruce@example.com");
    System.out.println("firstName = " + firstName);
}
```

Run the test method.

The query SQL logs:

```sql
select s1_0.first_name from t_student s1_0 where s1_0.email_address=?
```

Notes:
- The where cause is `where s1_0.email_address=?`

The query result:

```text
firstName = Bruce
```

## Native Query

Use native SQL queries with the `@Query` annotation by setting the `nativeQuery` attribute to true

### Get a record

Add a customized native query method in the `StudentRepository` class.

```java
@Query(
        value = "select s.* from t_student s where s.email_address = ?1",
        nativeQuery = true
)
Student getStudentByEmailAddressNative(String emailAddress);
```

Notes:
- The query is SQL syntax because `nativeQuery` is `true`.
- Use `t_student` table name not `Student` entity class name
- Use `email_address` column not `emailId` field
- `?1` is the first parameter
- It is `select s.*` not `select s`

Create the related test method:

```java
@Test
public void testGetStudentByEmailAddressNative() {
    Student student = studentRepository.getStudentByEmailAddressNative("bruce@example.com");
    System.out.println("student = " + student);
}
```

Run the test method.

The query SQL logs:

```sql
select s.* from t_student s where s.email_address = ?
```

Notes:
- Use native SQL not generated SQL
- The where cause is `where s.email_address = ?`

The query result:

```text
student = Student(studentId=1, firstName=Bruce, lastName=Lee, emailId=bruce@example.com, guardian=Guardian(name=Ye Wen, email=yewen@example.com, mobile=12345678))
```

### Get a field of record

Add a customized native query method in the `StudentRepository` class.

```java
@Query(
    value = "select s.first_name from t_student s where s.email_address = :email",
    nativeQuery = true
)
String getStudentFirstNameByEmailAddressNative(@Param("email") String emailAddress);
```

Notes:
- The query is SQL syntax because `nativeQuery` is `true`.
- Use `t_student` table name not `Student` entity class name
- Use `email_address` column not `emailId` field
- Use `@Param("email")` to define a named query parameter, and use `:email` to refer the parameter in JPQL
- It is `select s.first_name` not `select s.firstName`

Create the related test method:

```java
@Test
public void testGetStudentFirstNameByEmailAddressNative() {
    String firstName = studentRepository.getStudentFirstNameByEmailAddressNative("bruce@example.com");
    System.out.println("firstName = " + firstName);
}
```

Run the test method.

The query SQL logs:

```sql
select s.first_name from t_student s where s.email_address = ?
```

Notes:
- Use native SQL not generated SQL
- The where cause is `where s.email_address = ?`

The query result:

```text
firstName = Bruce
```

## @Transactional and @Modifying Annotation

### @Transactional

The `@Transactional` annotation in Spring Data JPA is used to define transactional boundaries around your repository methods.

When you annotate a method with `@Transactional`, Spring will automatically create a transaction before the method is executed, and commit or rollback the transaction when the method completes. 

By default, transactions are committed automatically if the method completes successfully, and rolled back if an exception is thrown.

Note that you should use the `@Transactional` annotation with care, as it can have a significant impact on performance and concurrency. 

You should only apply the annotation to methods that require transactional behavior, and avoid annotating methods that perform read-only operations or other operations that do not require a transaction.

Service example use @Transactional to update two tables in a transaction:

```java
@Service
public class MyService {

    @Autowired
    private MyEntityRepository myEntityRepository;
    
    @Autowired
    private MyOtherEntityRepository myOtherEntityRepository;

    @Transactional
    public void updateEntities(MyEntity myEntity, MyOtherEntity myOtherEntity) {
        myEntityRepository.save(myEntity);
        myOtherEntityRepository.save(myOtherEntity);
    }
}
```

### @Modifying

Modifying Query reference:
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.modifying-queries

The `@Modifying` annotation is used in conjunction with the `@Query` annotation in Spring Data JPA to indicate that the query being executed is a non-selecting query that modifies the database. 

Examples of such queries include INSERT, UPDATE, DELETE, and CREATE.

Define an update method in `StudentRepository` class.

```java
    @Modifying
    @Transactional
    @Query("update Student set firstName = :firstName, lastName = :lastName where emailId = :email")
    int updateStudentNameByEmailId(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String emailId);
```

Notes:
- Use `@Modifying` to use `update` statement in `@Query` annotation
- The `@Query` annotation use JPQL syntax, you also can use SQL syntax when set `nativeQuery` is `true`
- Use `@Param` for named query parameters
- Use `@Transactional` explicitly (it is optional in this example, because only has one update operation)

Write the test method:

```java
    @Test
    public void testUpdateStudentNameByEmailId() {
        int state = studentRepository.updateStudentNameByEmailId("Bruce", "Li", "bruce@example.com");
        assertEquals(1, state);
    }
```

Run the test method.

The SQL logs:
```sql
update t_student set first_name=?,last_name=? where email_address=?
```

Check the data changed in MySQL Workbench.

## JPA One-To-One Relationship

Example:
- A course has one course material.
- A course material is related to one course.

### Entity Classes

Create `Couse` entity class.

```java
@Entity
@Table(name = "t_course")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private String title;
    private Integer credit;
}
```

Create `CourseMaterial` entity class.

```java
@Entity
@Table(name = "t_course_material")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseMaterialId;
    private String url;
}
```

### OneToOne Mapping

Need add `course_id` column in `t_course_material` table.

Add `@OneToOne` relationship in `CourseMaterial` entity class.

```java
    @OneToOne
    @JoinColumn(
            name = "course_id",
            referencedColumnName = "courseId"
    )
    private Course course;
```

Notes:
- Use `@OneToOne` to indicate it is one-to-one relationship
- Use `@JoinColumn` to indicate add `course_id` column in `t_course_material` table for join query, and this column is related to `courseId` field of `Course` entity class.

Run the application:
```bash
mvn spring-boot:run
```

Check the SQL logs:

```sql
create table t_course (course_id bigint not null auto_increment, credit integer, title varchar(255), primary key (course_id)) engine=InnoDB
create table t_course_material (course_material_id bigint not null auto_increment, url varchar(255), course_id bigint, primary key (course_material_id)) engine=InnoDB
alter table t_course_material add constraint FK4wejvmqsnpc6urf89yd2nufnl foreign key (course_id) references t_course (course_id)
```

Notes:
- Create `t_course` and `t_course_material` tables
- The `t_course_material` table has a `course_id` column
- Add foreign key in `t_course_material` with `course_id` column and refer to `t_course.course_id` column

### Repositories

Create repositories classes.


The `CourseRepository` class:

```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
```

The `CourseMaterialRepository` class:

```java
@Repository
public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
}
```

### Repository Testing

Create a test class of `CourseMaterialRepository` as `CourseMaterialRepositoryTest`.

The `CourseMaterialRepositoryTest` test class:

```java
@SpringBootTest
class CourseMaterialRepositoryTest {

    @Autowired
    private CourseMaterialRepository courseMaterialRepository;

    @Test
    public void testSaveCourseMaterial() {
        Course course = Course.builder()
                .title("Data Science")
                .credit(6)
                .build();

        CourseMaterial courseMaterial = CourseMaterial.builder()
                .url("www.example.com")
                .course(course)
                .build();

        courseMaterialRepository.save(courseMaterial);
    }
}
```

Run the test method.

An error occurred:
```text
org.springframework.dao.InvalidDataAccessApiUsageException: 
  org.hibernate.TransientPropertyValueException: 
    object references an unsaved transient instance - 
      save the transient instance before flushing : 
        com.example.jpamysql.entity.CourseMaterial.course -> com.example.jpamysql.entity.Course
```

The application try to save course material and course together, but it failed, because we missed the CASCADE.

## Cascade Types

In Spring Data JPA, the cascade feature allows you to specify how changes to entities should be propagated to associated entities. 

The cascade options are specified using the cascade attribute in the relationship annotation.

In `CourseMaterial` entity class, add cascade.

```java
    @OneToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "course_id",
            referencedColumnName = "courseId"
    )
    private Course course;
```

Notes:
- The `CascadeType.AL`L, which means that any changes made to the `CourseMaterial` entity will be cascaded to the associated `Course` entities.

Re-run the test method `testSaveCourseMaterial()`.

It will succeed this time.

Check data changed in MySQL workbench:

```sql
select * from t_course;
select * from t_course_material;
```

## Fetch Types

In Spring Data JPA, the fetch type determines how the associated entities are loaded from the database when a query is executed. 


In Spring Data JPA, the fetch type determines how the associated entities are loaded from the database when a query is executed. There are two fetch types available:

1. `EAGER`: With the `EAGER` fetch type, the associated entities are loaded immediately along with the parent entity. 
  This can be useful when you always need the associated entities and want to minimize the number of queries executed. However, it can also lead to performance issues if there are many associated entities.

2. `LAZY`: With the `LAZY` fetch type, the associated entities are loaded only when they are actually accessed. 
  This can be more efficient if there are many associated entities, as they are only loaded when needed. However, it can also lead to the N+1 query problem, where multiple queries are executed to load the associated entities.

Configure fetch type for `CourseMaterial` entity class.

```java
    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "course_id",
            referencedColumnName = "courseId"
    )
    private Course course;
```

Add a test method in `CourseMaterialRepositoryTest` test class.

```java
    @Test
    public void printAllCourseMaterials() {
        List<CourseMaterial> courseMaterialList = courseMaterialRepository.findAll();
        System.out.println("courseMaterialList = " + courseMaterialList);
    }
```

Run the test method.

An error occurred:
```text
org.hibernate.LazyInitializationException: could not initialize proxy [com.example.jpamysql.entity.Course#1] - no Session
```

Exclude `course` fields in `CourseMaterial` entity class.

```java
@ToString(exclude = {"course"})
```

Notes:
- When use `FetchType.LAZY`, the course is not initialized, cause this problem.
- When use `FetchType.EAGER`, cause the `StackOverFlowError`.

Re-run the test method.

It will succeed.

The SQL logs:
```sql
select c1_0.course_material_id,c1_0.course_id,c1_0.url from t_course_material c1_0
```

The print result:
```text
[CourseMaterial(courseMaterialId=2, url=www.example.com)]
```

Notes:
- The result doesn't include the `Course`, it is not expected. We will fix it soon.


## Uni & Bi directional relationship

### CourseRepository Test

Create a test class of `CourseRepository` as `CourseRepositoryTest`.

```java
@SpringBootTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void printAllCourses() {
        List<Course> courses = courseRepository.findAll();
        System.out.println("courses = " + courses);
    }
}
```

Run the test method.

The SQL logs:

```sql
select c1_0.course_id,c1_0.credit,c1_0.title from t_course c1_0
```

The print result:

```text
[Course(courseId=1, title=Data Science, credit=6)]
```

We want to have `CourseMaterial` data in `Course` instance, how to do it?

### Bi-directional relationship

Add One-to-One relationship in `Course` entity class.

```java
    @OneToOne(
            mappedBy = "course"
    )
    private CourseMaterial courseMaterial;
```

Notes:
-  With `mappedBy` attribute pointing to the `course` attribute of the `CourseMaterial` entity

Re-run the test method `printAllCourses()` in `CourseRepositoryTest`.

The SQL logs:

```sql 
select c1_0.course_id,c1_0.credit,c1_0.title from t_course c1_0
select c1_0.course_material_id,c1_0.course_id,c1_0.url from t_course_material c1_0 where c1_0.course_id=?
```

Notes:
- It run two single query from each table instead of running a join query from two tables.

The print result:

```text 
[Course(courseId=1, title=Data Science, credit=6, courseMaterial=CourseMaterial(courseMaterialId=2, url=www.example.com))]
```

## JPA One-to-Many relationship

Example:
- A teacher can teach many courses.
- A course only has one teacher.

### Entities Class

Create `Teacher` entity class.

```java
@Entity
@Table(name = "t_teacher")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Teacher {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long teacherId;
  private String firstName;
  private String lastName;

  @OneToMany(
          cascade = CascadeType.ALL
  )
  @JoinColumn(
          name = "teacher_id",
          referencedColumnName = "teacherId"
  )
  private List<Course> courses;
}
```

Run the application:

```bash
mvn spring-boot:run
```

The SQL logs:

```sql 
alter table t_course add column teacher_id bigint
create table t_teacher (teacher_id bigint not null auto_increment, first_name varchar(255), last_name varchar(255), primary key (teacher_id)) engine=InnoDB
alter table t_course add constraint FKqif388cc6nuitg5yp5viaqtvj foreign key (teacher_id) references t_teacher (teacher_id)
```


Notes:
- Add `teacher_id` in `t_course` table
- Create `t_teacher` table
- Create a foreign key in `t_course` table with `teacher_id` which refer to `t_teacher.teacher_id`.

Check the schema change in MySQL workbench.

### Repositories Class

The `TeacherRepository` class.

```java 
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
```

### Repository Test

Create a test class of `TeacherRepository` as `TeacherRepositoryTest`.

```java
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
}
```

Run the test method.

The SQL logs:
```sql 
insert into t_teacher (first_name, last_name) values (?, ?)
```

Check data changed in MySQL workbench:

```sql 
select * from t_teacher
```

Create a new test method `testSaveTeacherWithCourses` to save teacher and course together.

```java 
    @Test
    public void testSaveTeacherWithCourses() {
        Course courseJava = Course.builder()
                .title("Java")
                .credit(6)
                .build();

        Course courseVueJS = Course.builder()
                .title("VueJS")
                .credit(6)
                .build();

        Teacher teacher = Teacher.builder()
                .firstName("Feihong")
                .lastName("Huang")
                .courses(List.of(courseJava, courseVueJS))
                .build();

        teacherRepository.save(teacher);
    }
```

Run the test method.

The SQL logs:

```sql 
insert into t_teacher (first_name, last_name) values (?, ?)
insert into t_course (credit, title) values (?, ?)
insert into t_course (credit, title) values (?, ?)
update t_course set teacher_id=? where course_id=?
update t_course set teacher_id=? where course_id=?
```

Check the data changed in MySQL workbench:

```sql 
select c.*, t.* from t_course c left join t_teacher t on c.teacher_id = t.teacher_id
where t.first_name = 'Feihong' and t.last_name = 'Huang'
```

### Optional as false

Set `optional` as `false` in `CourseMaterial` means a course material must have a course.

```java
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
```

## JPA Many-to-One Relationship

### Use `Many-to-One` relationship over `One-to-Many`

Comment the `@OneToMany` statements in `Teacher` entity class.

```java
//    @OneToMany(
//            cascade = CascadeType.ALL
//    )
//    @JoinColumn(
//            name = "teacher_id",
//            referencedColumnName = "teacherId"
//    )
//    private List<Course> courses;
```

Comment the related `testSaveTeacherWithCourses()` test method in `TeachRepositoryTest` test class. 

Add `@ManyToOne` relationship in `Course` entity class.

```java
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "teacher_id",
            referencedColumnName = "teacherId"
    )
    private Teacher teacher;
```

### Repository Test

Add test method `testSaveCoursesWithTeacher()` in `CourseRepositoryTest` test class.

```java
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
```

Run the test method.

The SQL logs:

```sql 
insert into t_teacher (first_name, last_name) values (?, ?)
insert into t_course (credit, teacher_id, title) values (?, ?, ?)
```

Notes:
- Add a new teacher
- Add a new course with the teacher

Check data changed in MySQL workbench:

```sql 
select c.*, t.* from t_course c left join t_teacher t on c.teacher_id = t.teacher_id
where t.first_name = 'Lei' and t.last_name = 'Li'
```

## Paging and Sorting

Check `JpaRepository` interface:

```java
@NoRepositoryBean
public interface JpaRepository<T, ID> extends ListCrudRepository<T, ID>, ListPagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T> {
    // ...
}
```

Notes:
- The `JpaRepository` supports paging and sorting.


### Test Paging

Add `findAllPagination()` test method in `CourseRepositoryTest` class.

```java
    @Test
    public void findAllPagination() {
        Pageable firstPageWithThreeRecords = PageRequest.of(0, 3);
        List<Course> courses = courseRepository.findAll(firstPageWithThreeRecords)
                .getContent();
        System.out.println("courses = " + courses);
    }
```

The SQL logs is like:

```sql 
select c1_0.course_id,c1_0.credit,c1_0.teacher_id,c1_0.title from t_course c1_0 limit ?,?
select c1_0.course_material_id,c1_0.course_id,c1_0.url from t_course_material c1_0 where c1_0.course_id=?
select c1_0.course_material_id,c1_0.course_id,c1_0.url from t_course_material c1_0 where c1_0.course_id=?
select t1_0.teacher_id,t1_0.first_name,t1_0.last_name from t_teacher t1_0 where t1_0.teacher_id=?
select c1_0.course_material_id,c1_0.course_id,c1_0.url from t_course_material c1_0 where c1_0.course_id=?
select count(c1_0.course_id) from t_course c1_0
```

Notes:
- Query by `limit` to narrow the result set
- Calculate the total count
- Query course material from `t_course_material` table with `course_id`
- Query teacher from `t_teacher` table with `teacher_id`


Modify the test method to print more information.

```java
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
```

Re-run the test method.

### Test Sorting

#### Simple sorting

Define a method in `CourseRepository` class.

```java
List<Course> findByOrderByTitleAsc();
```

Create a related test method in `CourseRepositoryTest` class.

```java
    @Test
    public void testFindByOrderByTitleAsc() {
        List<Course> courses = courseRepository.findByOrderByTitleAsc();
        System.out.println("courses = " + courses);
    }
```

Do similar thing for order by desc.

Or use `Sort.by()` for dynamic sorting.

```java
    @Test
    public void testFindByDynamicSort() {
        List<Course> courses = courseRepository.findAll(Sort.by("title").ascending());
        System.out.println("courses = " + courses);
    }
```

#### Sorting with paging

Define a new test method.

```java
    @Test
    public void testSortingWithPaging() {
        Pageable sortByTitleAndCredit = PageRequest.of(0, 2,
                Sort.by("title").descending().and(Sort.by("credit").ascending()));
        Page<Course> coursePage = courseRepository.findAll(sortByTitleAndCredit);
        
        List<Course> courses = coursePage.getContent();
        System.out.println("courses = " + courses);
    }
```

Run the test method.

The main query SQL logs:

```sql 
select c1_0.course_id,c1_0.credit,c1_0.teacher_id,c1_0.title from t_course c1_0 order by c1_0.title desc,c1_0.credit asc limit ?,?
```

## JPA Many-to-Many relationship

Example:
- A student has many courses.
- A course has many students.

Add Many-to-Many relationship.

```java
    @ManyToMany(
        cascade = CascadeType.ALL
)
@JoinTable(
        name = "t_student_course",
        joinColumns = @JoinColumn(
                name = "course_id",
                referencedColumnName = "courseId"
        ),
        inverseJoinColumns = @JoinColumn(
                name = "student_id",
                referencedColumnName = "studentId"
        )
)
private List<Student> students;
```

Add `@ToString(exclude = {"students"})` in the `Course` entity class.

Run the application:

```bash
mvn spring-boot:run
```

Notes:
- Create `t_student_course` table with `student_id` and `course_id` columns

### Repository Test

Add a test method in `CourseRepositoryTest` class.

```java
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
```

Run the test method.

The SQL logs:

```sql 
insert into t_teacher (first_name, last_name) values (?, ?)
insert into t_course (credit, teacher_id, title) values (?, ?, ?)
insert into t_student (email_address, first_name, guardian_email, guardian_mobile, guardian_name, last_name) values (?, ?, ?, ?, ?, ?)
insert into t_student (email_address, first_name, guardian_email, guardian_mobile, guardian_name, last_name) values (?, ?, ?, ?, ?, ?)
insert into t_student_course (course_id, student_id) values (?, ?)
insert into t_student_course (course_id, student_id) values (?, ?)
```

Notes:
- Add a course record
- Add a teacher record
- Add student records
- Add student-course mapping records

Check data changed in MySQL workbench:

```sql 
select c.*, t.*, s.*
  from t_course c 
  left join t_teacher t on c.teacher_id = t.teacher_id
  left join t_student_course sc on c.course_id = sc.course_id
  left join t_student s on sc.student_id = s.student_id;
```

## Run all-in-one

Restart the MySQL container:
```bash
podman stop local-mysql

podman run --rm --name local-mysql \
  -e MYSQL_ROOT_PASSWORD=admin123 \
  -p 3306:3306 \
  -d mysql
```

Use MySQL workbench to connect to the MySQL.

Create the schema:

```sql 
CREATE SCHEMA `schooldb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
```

Switch schema:

```bash
use schooldb;
```

Build the application:
```bash
mvn clean install
```





