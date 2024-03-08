package org.javafxtest.repository;

import org.javafxtest.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Nikolay Boyko
 */

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Long> {

    @Query(value = "SELECT * FROM news_table WHERE news_name=:name ORDER BY publication_time DESC LIMIT 1", nativeQuery = true)
    NewsEntity getLatestNewsFor(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM news_table WHERE DATE(publication_time) <> :todayDate", nativeQuery = true)
    void deleteRowsNotToday(@Param("todayDate") LocalDate todayDate);

    @Query(value = "SELECT IF (COUNT(publication_time) > 0, 'true', 'false') FROM news_table WHERE DATE(publication_time) <> :todayDate", nativeQuery = true)
    boolean hasRowsNotToday(@Param("todayDate") LocalDate todayDate);
}
