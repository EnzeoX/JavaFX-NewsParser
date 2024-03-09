package org.javafxtest.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.model.NewsModel;
import org.javafxtest.repository.NewsRepository;
import org.javafxtest.utils.EntityModelMapper;
import org.javafxtest.utils.parser.AbstractParser;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final List<String> parserNames = new ArrayList<>();
    private final Map<String, AbstractParser> parserMap = new HashMap<>();

    public NewsService(List<AbstractParser> parsers, NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
        for (AbstractParser parser : parsers) {
            if (parser != null) {
                parserMap.put(parser.getParserName(), parser);
                parserNames.add(parser.getParserName());
            }
        }
    }

    public void removeAllRecordsForNotToday() {
        this.newsRepository.deleteRowsNotToday(LocalDate.now());
    }

    public boolean checkForUpdates() {
        try {
            long totalNewsInDb = this.newsRepository.count();
            List<NewsModel> listOfLatestNews = new LinkedList<>();
            if (totalNewsInDb > 0) {      // DB already contains some news, and just needs to be updated if so
                log.info("Database contains news, updating...");
                for (String newsName : parserNames) {
                    List<NewsModel> newsModelList = getLatestNewsForName(newsName);
                    if (newsModelList.size() > 0) {
                        listOfLatestNews.addAll(newsModelList);
                    }
                }
                if (listOfLatestNews.size() > 0) {
                    log.info("Bunch of new news, adding data. List size: {}", listOfLatestNews.size());
                    saveAllNews(listOfLatestNews);
                }
            } else {                                    // No data in DB, load all
                log.info("Database is empty, trying to get some news...");
                for (String name : parserNames) {
                    List<NewsModel> parsedNews = parserMap.get(name).parseNewsResource();
                    if (parsedNews.size() > 0) {
                        listOfLatestNews.addAll(parsedNews);
                    }
                }
            }

            if (listOfLatestNews.size() > 0) {
                // before all check if need to delete old records
                if (this.newsRepository.hasRowsNotToday(LocalDate.now())) {
                    log.info("Database contains old data, removing");
                    removeAllRecordsForNotToday();
                } else {
                    log.info("Database don't have old data");
                }
                saveAllNews(listOfLatestNews);
                return totalNewsInDb < listOfLatestNews.size();
            } else {
                return false;
            }
        } catch (Exception sqlException) {
            log.error("SQL error message: {}", sqlException.getMessage());
            return false;
        }
    }

    public void saveAllNews(List<NewsModel> listOfModels) {
        if (listOfModels == null || listOfModels.size() == 0) {
            log.warn("Provided list of models is null or empty!");
            return;
        }
        List<NewsEntity> newsEntityList = EntityModelMapper.listOfModelsToEntity(listOfModels);
        try {
//            this.newsRepository.saveAll(newsEntityList);
            if (newsEntityList.size() > 0) {
                for (NewsEntity entity : newsEntityList) {
                    saveNews(entity);
                }
            }
        } catch (Exception e) {
            log.error("SQL error message: {}", e.getMessage());
        }
    }

    public void saveNews(NewsEntity entity) {
        this.newsRepository.save(entity);
    }

    public void saveNews(NewsModel data) {
        NewsEntity newsEntity = EntityModelMapper.newsModelToEntity(data);
        this.newsRepository.save(newsEntity);
    }

    public void deleteNews(NewsModel data) {
        NewsEntity newsEntity = EntityModelMapper.newsModelToEntity(data);
        this.newsRepository.delete(newsEntity);
    }

    public void deleteAllNews() {
        this.newsRepository.deleteAll();
    }

    public List<NewsModel> getAllNews() {
        List<NewsEntity> entityList = this.newsRepository.getAllNewsOrdered();
        return EntityModelMapper.listOfEntitiesToListOfModels(entityList);
    }

    public List<NewsModel> getNewsForTimePeriod(String timePeriod) {
        LocalDateTime timeFrom;
        LocalDateTime timeTo;
        switch (timePeriod) {
            case "morning":
                timeFrom = LocalDate.now().atTime(6, 0).minusDays(1);
                timeTo = LocalDate.now().atTime(8, 59, 59).minusDays(1);
                break;
            case "day":
                timeFrom = LocalDate.now().atTime(9, 0).minusDays(1);
                timeTo = LocalDate.now().atTime(15, 59,59).minusDays(1);
                break;
            case "evening":
                timeFrom = LocalDate.now().atTime(16, 0).minusDays(1);
                timeTo = LocalDate.now().atTime(20, 59, 59).minusDays(1);
                break;
            case "all":
            default:
                timeFrom = LocalDate.now().atTime(0, 0).minusDays(1);
                timeTo = LocalDate.now().atTime(23, 59, 59).minusDays(1);
                break;
        }
        List<NewsEntity> list = this.newsRepository.getNewsBetween(timeFrom, timeTo);
        return EntityModelMapper.listOfEntitiesToListOfModels(list);
    }

    public NewsEntity getActualNewsFor(String newsName) {
        return this.newsRepository.getLatestNewsFor(newsName);
    }

    private List<NewsModel> getLatestNewsForName(String newsName) {
        NewsEntity latestNewsEntity = getActualNewsFor(newsName);
        List<NewsModel> parsedNews = parserMap.get(newsName).parseNewsResource();
        List<NewsModel> newNewsToAdd = new LinkedList<>();
        for (NewsModel model : parsedNews) {
            if (model == null) {
                continue;
            }
            if (model.getPublicationTime().isAfter(latestNewsEntity.getPublicationTime())) {
                log.info("New news has arrived");
                newNewsToAdd.add(model);
            }
        }
        return newNewsToAdd;
    }
}
