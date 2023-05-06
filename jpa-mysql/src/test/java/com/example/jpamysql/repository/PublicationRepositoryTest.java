package com.example.jpamysql.repository;

import com.example.jpamysql.entity.Article;
import com.example.jpamysql.entity.Publication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class PublicationRepositoryTest {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void testSavePublicationAndArticles() {
        // save publication 1
        Publication publication1 = new Publication();
        publication1.setName("pb_name1");
        publication1.setCategory("technology");

        Article article1 = Article.builder()
                .title("title1")
                .publication(publication1)
                .build();
        Article article2 = Article.builder()
                .title("title2")
                .publication(publication1)
                .build();

        publication1.getArticles().add(article1);
        publication1.getArticles().add(article2);
        publicationRepository.save(publication1);

        // save publication 2
        Publication publication2 = new Publication();
        publication2.setName("pb_name2");
        publication2.setCategory("technology");

        Article article3 = Article.builder()
                .title("title3")
                .publication(publication2)
                .build();

        publication2.getArticles().add(article3);
        publicationRepository.save(publication2);
    }
    
    @Test
    public void testFindByCategory() {
        List<Publication> publications = publicationRepository.findByCategory("technology");
        printPublications(publications);
    }

    private static void printPublications(List<Publication> publications) {
        for (Publication publication : publications) {
            for (Article article : publication.getArticles()) {
                log.info("publicationId={}, name={}, category={}, articleId={}, title={}",
                        publication.getPublicationId(),
                        publication.getName(),
                        publication.getCategory(),
                        article.getArticleId(),
                        article.getTitle());
            }
        }
    }

    @Test
    public void testQueryByCategoryWithLefJoinFetch() {
        List<Publication> publications = publicationRepository.queryByCategory("technology");
        printPublications(publications);
    }

}