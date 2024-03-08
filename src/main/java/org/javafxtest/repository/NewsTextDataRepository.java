package org.javafxtest.repository;

import org.javafxtest.entity.NewsTextData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Nikolay Boyko
 */
public interface NewsTextDataRepository extends JpaRepository<NewsTextData, Long> {
}
