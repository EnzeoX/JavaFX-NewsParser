package org.javafxtest.controller.gui;

import javafx.application.HostServices;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    //    @Autowired
    private NewsService newsService;

    @FXML
    private GridPane bottomGridPane;

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
//        headerBackToMenuButton.setOnAction(this::backToMainMenu);
//        bottomPreviousNewsButton.setOnAction(action -> {
//            log.info("Is news service not null? {}", this.newsService);
//        });
//        bottomNextNewsButton.setOnAction(action -> {
//        });
//        reloadNewsButton.setOnAction(actionEvent -> {
//            reloadNews();
//        });
        nextNewsButton.setOnAction(actionEvent -> {
            reloadNews();
        });
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

    private void reloadNews() {
        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
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
                NewsModel newsModel = newsModelList.get(0);
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
                    fullHtml.append("/div>");
                    mainWebView.getEngine().loadContent(fullHtml.toString(), "text/html");

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

                }

//                if (newsTextData.getChildrenTextData() != null) {
//                    String textToSetInWebView = newsTextData.getHtmlString();
//                    mainNewsView.getEngine().loadContent(textToSetInWebView, "text/html");
//                }
            }
        });

        updateTask.setOnFailed(evt -> {
            // we're on the JavaFX application thread here
            log.warn("News not updated!");
        });
        new Thread(updateTask).start();
    }

    private void nextNews() {

    }
}
