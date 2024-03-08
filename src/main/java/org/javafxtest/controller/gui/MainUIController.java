package org.javafxtest.controller.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.javafxtest.model.NewsModel;
import org.javafxtest.service.NewsService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Component
@Scope("prototype")
public class MainUIController {

    private final ApplicationContext applicationContext;

    private final List<NewsModel> availableNews = new ArrayList<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final AtomicBoolean isExecutingPage = new AtomicBoolean(false);

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
        previousNewsButton.setOnAction(actionEvent -> {
            if (!isExecutingPage.get()) {
                Platform.runLater(() -> {
                    isExecutingPage.set(true);
                    showPreviousNews(actionEvent);
                    isExecutingPage.set(false);
                });
            }
        });
//        mainWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
//            if (newState == Worker.State.SUCCEEDED) {
//                // new page has loaded, process:
//                log.info("Page loaded");
//            }
//        });
        nextNewsButton.setOnAction(actionEvent -> {
            if (!isExecutingPage.get()) {
                Platform.runLater(() -> {
                    isExecutingPage.set(true);
                    showNextNews(actionEvent);
                    isExecutingPage.set(false);
                });
            }
        });
        reloadNews();
    }

    private void backToMainMenu(ActionEvent actionEvent) {
        Stage stage;
        try {
            executor.shutdownNow();
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
            fullHtml.append(newsModel.getDivMediaSource());
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

    private void showPreviousNews(ActionEvent actionEvent) {
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            ((Button) actionEvent.getSource()).setDisable(true);
        }
        if (availableNews.size() > 0 && currentNewsIndex - 1 > 0) {
            currentNewsIndex -= 1;
            log.info("Current page counter: {}", currentNewsIndex);
            String fullHtml = getNewsAsHtml();
            mainWebView.getEngine().loadContent(fullHtml, "text/html");
        }
        if (nextNewsButton.isDisabled()) {
            nextNewsButton.setDisable(false);
        }
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            if (currentNewsIndex != 0) {
                ((Button) actionEvent.getSource()).setDisable(false);
            }

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


    private void showNextNews(ActionEvent actionEvent) {
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            ((Button) actionEvent.getSource()).setDisable(true);
        }
        if (availableNews.size() > 0 && currentNewsIndex < availableNews.size()) {
            String fullHtml = getNewsAsHtml();
            currentNewsIndex += 1;
            log.info("Current page counter: {}", currentNewsIndex);
            log.info("Available news: {}", availableNews.size());
            mainWebView.getEngine().loadContent(fullHtml, "text/html");
        }
        if (previousNewsButton.isDisabled()) {
            previousNewsButton.setDisable(false);
        }
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            if (currentNewsIndex + 1 != availableNews.size()) {
                ((Button) actionEvent.getSource()).setDisable(false);
            }
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
            log.info("News updated!");
            List<NewsModel> newsModelList = newsService.getAllNews();
            if (newsModelList != null && newsModelList.size() > 0) {
                log.info("There is some news...");
                availableNews.addAll(newsModelList);
                log.info("News loaded and added!");
                showNextNews(null);
            } else {
                log.warn("No news to show");
            }
        });

        updateTask.setOnFailed(evt -> {
            log.error("News not updated!");
        });
        executor.execute(new Thread(updateTask));
    }
}
