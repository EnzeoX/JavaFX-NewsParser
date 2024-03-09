package org.javafxtest.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_news", nullable = false)
    private NewsEntity news;
}
