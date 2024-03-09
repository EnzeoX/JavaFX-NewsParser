package org.javafxtest.utils;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.entity.NewsTextData;
import org.javafxtest.model.NewsModel;
import org.javafxtest.model.TextData;
import org.jsoup.parser.Tag;

import java.util.HashSet;
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
        newsModel.setNewsDescription(entity.getNewsDescription());
        newsModel.setPublicationTime(entity.getPublicationTime());
        newsModel.setUrlMediaSource(entity.getNewsMediaUrl());
        newsModel.setSourceType(Tag.valueOf(entity.getNewsHeaderMedia()));
        List<String> newsTextData = new LinkedList<>();
        for (NewsTextData textData : entity.getNewsTextData()) {
            if (textData != null) {
                newsTextData.add(textData.getTextData());
            }
        }
        newsModel.setNewsTextData(newsTextData);
        return newsModel;
    }

    public static NewsEntity newsModelToEntity(NewsModel model) {
        Objects.requireNonNull(model, "Provided model is null!");
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setNewsName(model.getNewsResourceName());
        newsEntity.setNewsHeadline(model.getNewsHeadline());
        newsEntity.setNewsDescription(model.getNewsDescription());
        newsEntity.setPublicationTime(model.getPublicationTime());
        newsEntity.setNewsMediaUrl(model.getUrlMediaSource());
        newsEntity.setNewsHeaderMedia(model.getSourceType().getName());
        List<NewsTextData> newsTextDataList = new LinkedList<>();
        if (model.getNewsData() != null) {
            for (TextData text : model.getNewsData().getChildrenTextData()) {
                NewsTextData newsTextData = new NewsTextData();
                newsTextData.setNews(newsEntity);
                newsTextData.setTextData(text.getHtmlString());
                newsTextDataList.add(newsTextData);
            }
//            newsEntity.setNewsTextData(newsTextDataList);
        } else if (model.getNewsTextData() != null) {
            for (String text : model.getNewsTextData()) {
                NewsTextData newsTextData = new NewsTextData();
                newsTextData.setNews(newsEntity);
                newsTextData.setTextData(text);
                newsTextDataList.add(newsTextData);
            }
        }
//        newsEntity.setNewsTextData(newsTextDataList);
        if (newsEntity.getNewsTextData() == null) {
            newsEntity.setNewsTextData(new HashSet<>());
        }
        newsEntity.getNewsTextData().addAll(newsTextDataList);
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
