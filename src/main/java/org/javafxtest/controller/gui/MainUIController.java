package org.javafxtest.controller.gui;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.model.NewsModel;
import org.javafxtest.model.TextData;
import org.javafxtest.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Component
@Scope("prototype")
public class MainUIController {

    private final ApplicationContext applicationContext;

    private final List<NewsModel> availableNews = new ArrayList<>();

    private int currentNewsIndex = 0;

    //    @Autowired
    private NewsService newsService;

    @FXML
    private VBox bodyPane;

    @FXML
    private GridPane bottomGridPane;

    @FXML
    private Label currentNewsLabel;

    @FXML
    private HBox currentNewsPane;

    @FXML
    private GridPane headerGridPane;

    @FXML
    private GridPane mainGridPane;

    @FXML
    private VBox mainVBoxPane;

    @FXML
    private WebView mainWebView;

    @FXML
    private Button nextNewsButton;

    @FXML
    private Button previousNewsButton;

    @FXML
    private Label timePeriodLabel;

    @FXML
    private MenuButton timePeriodSelection;

    @FXML
    private Button toMainMenuButton;

    public MainUIController(ApplicationContext applicationContext, NewsService newsService) {
        this.newsService = newsService;
        this.applicationContext = applicationContext;
    }

    @FXML
    void initialize() {
        toMainMenuButton.setOnAction(this::backToMainMenu);
        nextNewsButton.setOnAction(actionEvent -> {
            showNextNews();
        });
        previousNewsButton.setOnAction(actionEvent -> {
            showPreviousNews();
        });
        reloadNews();
    }

    private void backToMainMenu(ActionEvent actionEvent) {
        Stage stage;
        try {
            Scene currentScene = toMainMenuButton.getScene();
            stage = (Stage) currentScene.getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/start.fxml"));
            fxmlLoader.setControllerFactory(this.applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNewsAsHtml() {
        NewsModel newsModel = availableNews.get(currentNewsIndex);
        if (newsModel.getNewsTextData() != null && newsModel.getNewsTextData().size() > 0) {
            StringBuilder fullHtml = new StringBuilder();
            fullHtml.append("<div>");
            fullHtml.append("<div>")
                    .append("<img src=\"")
                    .append("https://i.stack.imgur.com/SBv4T.gif")
                    .append("alt=\"Be patient...\" width=\"300px\"/>").append("</div>");
            fullHtml.append(newsModel.getHeadlineAsHtml());
            fullHtml.append(newsModel.getDescriptionAsHtml());
            fullHtml.append(newsModel.getNewsPublicationAsHtml());
            for (String newsText : newsModel.getNewsTextData()) {
                if (newsText != null) {
                    fullHtml.append(newsText);
                }
            }
            fullHtml.append("</div>");
            return fullHtml.toString();
        }
        return "";
    }

    private void showPreviousNews() {
        if (availableNews.size() > 0 && currentNewsIndex > 0) {
            currentNewsIndex -= 1;
            String fullHtml = getNewsAsHtml();
            mainWebView.getEngine().loadContent(fullHtml, "text/html");

        }
    }

    // FOR FULLY LOADED PAGE
//                    webview.getEngine().getLoadWorker().stateProperty().addListener(
//                            new ChangeListener<Worker.State>() {
//                                @Override
//                                public void changed(
//                                        ObservableValue<? extends Worker.State> observable,
//                                        Worker.State oldValue, Worker.State newValue) {
//                                    switch (newValue) {
//                                        case SUCCEEDED:
//                                        case FAILED:
//                                        case CANCELLED:
//                                            webview
//                                                    .getEngine()
//                                                    .getLoadWorker()
//                                                    .stateProperty()
//                                                    .removeListener(this);
//                                    }
//
//
//                                    if (newValue != Worker.State.SUCCEEDED) {
//                                        return;
//                                    }
//
//                                    // Your logic here
//                                    System.out.println("page loaded");
//                                }
//                            } );


    private void showNextNews() {
        if (availableNews.size() > 0 && currentNewsIndex <= availableNews.size()) {
            currentNewsIndex += 1;
            String fullHtml = getNewsAsHtml();
            mainWebView.getEngine().loadContent(fullHtml, "text/html");
        }
    }

    private void reloadNews() {
        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() {
                newsService.checkForUpdates();
                return null;
            }
        };
        updateTask.setOnSucceeded(evt -> {
            // we're on the JavaFX application thread here
            log.info("News updated!");
            List<NewsModel> newsModelList = newsService.getAllNews();
            if (newsModelList != null && newsModelList.size() > 0) {
                log.info("There is some news...");
                availableNews.addAll(newsModelList);
                log.info("News loaded and added!");
                showNextNews();
//                if (newsTextData.getChildrenTextData() != null) {
//                    String textToSetInWebView = newsTextData.getHtmlString();
//                    mainNewsView.getEngine().loadContent(textToSetInWebView, "text/html");
//                }
            }
        });

        updateTask.setOnFailed(evt -> {
            log.error("News not updated!");
        });
        new Thread(updateTask).start();
    }
}
