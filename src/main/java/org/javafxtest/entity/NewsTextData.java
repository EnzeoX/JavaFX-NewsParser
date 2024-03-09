package org.javafxtest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
@Entity
@Table(name = "news_text_data_table")
public class NewsTextData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_text_data_id")
    private long newsTextDataId;

    @Column(name = "text_data")
    private String textData;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_news", nullable = false)
    private NewsEntity news;
}
