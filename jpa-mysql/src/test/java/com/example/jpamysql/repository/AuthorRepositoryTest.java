package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Author;
import com.example.jpamysql.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void testSaveAuthorsAndBooks() {
        Author author = new Author();
        author.setName("William");
        author.setNationality("American");

        Book book1 = Book.builder()
                .title("Java 101")
                .author(author)
                .build();

        Book book2 = Book.builder()
                .title("Container 101")
                .author(author)
                .build();

        author.getBooks().add(book1);
        author.getBooks().add(book2);
        authorRepository.save(author);
    }

    @Test
    public void testFindById() {
        // a single left join query
        Author author = authorRepository.findById(1L).orElse(null);
        assertNotNull(author);
        assertEquals(2, author.getBooks().size());
    }

    @Test
    public void testFindByName() {
        // N + 1 query
        Author author = authorRepository.findByName("William").orElse(null);
        assertNotNull(author);
        assertEquals(2, author.getBooks().size());
    }

    @Test
    public void testFindByIdWithBooks() {
        // a single left join query
        Author author = authorRepository.findByIdWithBooks(1L).orElse(null);
        assertNotNull(author);
        assertEquals(2, author.getBooks().size());
    }

    @Test
    public void testFindByNationality() {
        // a single left join query
        Author author = authorRepository.findByNationality("American").get(0);
        assertNotNull(author);
        assertEquals(2, author.getBooks().size());
    }
}