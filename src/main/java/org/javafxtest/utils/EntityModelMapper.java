package org.javafxtest.utils;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.entity.NewsTextData;
import org.javafxtest.model.NewsModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Nikolay Boyko
 */

@Slf4j
public class EntityModelMapper {

    public static NewsModel newsEntityToModel(NewsEntity entity) {
        Objects.requireNonNull(entity, "Provided entity model is null!");
        NewsModel newsModel = new NewsModel();
        newsModel.setNewsResourceName(entity.getNewsName());
        newsModel.setNewsHeadline(entity.getNewsHeadline());
        List<String> newsTextData = new LinkedList<>();
        for (NewsTextData textData : entity.getNewsTextData()) {
            if (textData != null) {
                newsTextData.add(textData.getTextData());
            }
        }
        newsModel.setNewsTextData(newsTextData);
        newsModel.setNewsDescription(entity.getNewsDescription());
        newsModel.setPublicationTime(entity.getPublicationTime());
        return newsModel;
    }

    public static NewsEntity newsModelToEntity(NewsModel model) {
        Objects.requireNonNull(model, "Provided model is null!");
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setNewsName(model.getNewsResourceName());
        newsEntity.setNewsHeadline(model.getNewsHeadline());
        newsEntity.setNewsDescription(model.getNewsDescription());
        newsEntity.setPublicationTime(model.getPublicationTime());
        List<NewsTextData> newsTextDataList = new LinkedList<>();
        for (String text : model.getNewsTextData()) {
            NewsTextData newsTextData = new NewsTextData();
            newsTextData.setNews(newsEntity);
            newsTextData.setTextData(text);
            newsTextDataList.add(newsTextData);
        }
        newsEntity.setNewsTextData(newsTextDataList);
        return newsEntity;
    }

    public static List<NewsEntity> listOfModelsToEntity(List<NewsModel> modelList) {
        Objects.requireNonNull(modelList, "Provided list of models if null!");
        List<NewsEntity> newsEntityList = new LinkedList<>();
        for (NewsModel model : modelList) {
            newsEntityList.add(newsModelToEntity(model));
        }
        return newsEntityList;
    }

    public static List<NewsModel> listOfEntitiesToListOfModels(List<NewsEntity> entityList) {
        Objects.requireNonNull(entityList, "Provided list of entities if null!");
        List<NewsModel> listOfModels = new LinkedList<>();
        for (NewsEntity entity : entityList) {
            listOfModels.add(newsEntityToModel(entity));
        }
        return listOfModels;
    }
}
