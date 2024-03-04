package org.javafxtest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
@Entity
@Table(name = "news_table")
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_news")
    private long idNews;

    @Column(name = "news_name")
    private String newsName;

    @Column(name = "news_headline")
    private String newsHeadline;

    @Column(name = "news_description")
    private String newsDescription;

    @Column(name = "publication_time")
    private LocalDateTime publicationTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "news", cascade = CascadeType.ALL)
    private List<NewsTextData> newsTextData;
}
