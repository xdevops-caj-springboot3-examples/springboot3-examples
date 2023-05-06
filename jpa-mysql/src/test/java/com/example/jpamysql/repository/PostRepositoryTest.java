package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void testSavePost() {
        Post post = Post.builder()
                .title("Spring Data JPA with MySQL")
                .content("Use Spring Data JPA to access MySQL database")
                .build();

        postRepository.save(post);
    }

    @Test
    public void testUpdatePost() {
        Post post = postRepository.findById(1L).orElseThrow();
        post.setContent("Enable JPA Auditing");

        postRepository.save(post);
    }

    @Test
    public void testSoftDeletePost() {
        postRepository.deleteById(1L);
    }
    
    @Test
    public void testFindAll() {
        List<Post> posts = postRepository.findAll();
        assertEquals(0, posts.size());
        System.out.println("posts = " + posts);
    }

}