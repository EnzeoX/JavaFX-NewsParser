package org.javafxtest.service;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.model.NewsModel;
import org.javafxtest.repository.NewsRepository;
import org.javafxtest.utils.EntityModelMapper;
import org.javafxtest.utils.parser.AbstractParser;
import org.springframework.stereotype.Service;

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

    public void checkForUpdates() {
        List<NewsModel> listOfLatestNews = new LinkedList<>();
        if (this.newsRepository.count() > 0) {      // DB already contains some news, and just needs to be updated if so
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
            saveAllNews(listOfLatestNews);
        }
    }

    public void saveAllNews(List<NewsModel> listOfModels) {
        if (listOfModels == null || listOfModels.size() == 0) {
            log.warn("Provided list of models is null or empty!");
            return;
        }
        List<NewsEntity> newsEntityList = EntityModelMapper.listOfModelsToEntity(listOfModels);
        this.newsRepository.saveAll(newsEntityList);
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
        List<NewsEntity> entityList = this.newsRepository.findAll();
        return EntityModelMapper.listOfEntitiesToListOfModels(entityList);
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

//    public NewsModel getSpecificNews(NewsFilter filter) {
//        return null;
//    }
}
