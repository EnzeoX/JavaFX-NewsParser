package org.javafxtest.repository;

import org.javafxtest.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Nikolay Boyko
 */

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {

    @Query(value = "SELECT * FROM news_table WHERE news_name=:name ORDER BY publication_time DESC LIMIT 1", nativeQuery = true)
    NewsEntity getLatestNewsFor(String name);

}
