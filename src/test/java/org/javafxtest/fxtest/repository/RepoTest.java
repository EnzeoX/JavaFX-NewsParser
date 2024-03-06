package org.javafxtest.fxtest.repository;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.entity.NewsTextData;
import org.javafxtest.model.NewsModel;
import org.javafxtest.repository.NewsRepository;

import org.javafxtest.utils.EntityModelMapper;
import org.javafxtest.utils.parser.impl.TheVergeNewsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepoTest {

    @Autowired
    private NewsRepository newsRepository;
    @InjectMocks
    private TheVergeNewsParser theVergeNewsParser;

    private NewsEntity newsEntity;
    private List<NewsTextData> newsTextDataList;

    @BeforeEach
    public void init() {
        newsEntity = new NewsEntity();
        newsEntity.setNewsName("TheTest");
        newsEntity.setNewsDescription("Testing description");
        newsEntity.setPublicationTime(LocalDateTime.now());
        newsEntity.setNewsHeadline("Test Headline");

        newsTextDataList = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            NewsTextData textData = new NewsTextData();
            if (i == 0) {
                textData.setTextData("Data start");
            } else if (i == 4) {
                textData.setTextData("...and data ends!");
            } else {
                textData.setTextData("...and data continues " + i);
            }
            textData.setNews(newsEntity);
            newsTextDataList.add(textData);
        }
        newsEntity.setNewsTextData(newsTextDataList);
    }

    @Test
    public void testAddingData() {
        log.info("Trying to save, watch out...");
        this.newsRepository.save(newsEntity);
    }

    @Test
    public void testAddAndCollect() {
        newsRepository.save(newsEntity);
        List<NewsEntity> collectedFromDBEntity = newsRepository.findAll();
        assertNotNull(collectedFromDBEntity);
        log.info("Collected");
        NewsTextData textData = collectedFromDBEntity.get(0).getNewsTextData().get(0);
        assertEquals("Data start", textData.getTextData());
    }

    @Test
    public void testGetDataAndSave() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "https://www.theverge.com");
        List<NewsModel> modelList = this.theVergeNewsParser.parseNewsResource(this.theVergeNewsParser.getResourceUrl());
        List<NewsEntity> newsEntityList = EntityModelMapper.listOfModelsToEntity(modelList);
        assertNotNull(newsEntityList);
        newsRepository.saveAll(newsEntityList);
    }
}
