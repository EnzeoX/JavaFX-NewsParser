package org.javafxtest.controller.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    private NewsModel currentNews = null;

    private String currentActiveItem = null;

    //    @Autowired
    private final NewsService newsService;

    @FXML
    private VBox bodyPane;

    @FXML
    private GridPane bottomGridPane;

    @FXML
    private HBox bottomHBox;

    @FXML
    private Label currentNewsLabel;

    @FXML
    private HBox currentNewsPane;

    @FXML
    private GridPane headerGridPane;

    @FXML
    private HBox headerHBox;

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
    private ComboBox<String> timePeriodSelection;

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
        String[] timePeriods = {"all", "morning", "day", "evening"};
        currentActiveItem = "all";
        timePeriodSelection.setItems(FXCollections.observableArrayList(timePeriods));
        timePeriodSelection.getSelectionModel().selectFirst();
        timePeriodSelection.setOnAction(actionEvent -> {
//            Platform.runLater(() -> {
                if (!currentActiveItem.equals(timePeriodSelection.getValue())) {
                    currentActiveItem = timePeriodSelection.getValue();
                    reloadNews(currentActiveItem);
                }
//            });
        });
        nextNewsButton.setOnAction(actionEvent -> {
            if (!isExecutingPage.get()) {
                Platform.runLater(() -> {
                    isExecutingPage.set(true);
                    showNextNews(actionEvent);
                    isExecutingPage.set(false);
                });
            }
        });
        reloadNews(currentActiveItem);
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

    private NewsModel getSelectedNewsModel() {
        return availableNews.get(currentNewsIndex);
    }

    private void setNewsNameLabel(String name) {
        this.currentNewsLabel.setText(name);
    }

    private String getNewsAsHtml(NewsModel newsModel) {
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
        if (currentNews != null) {
            int currentNewsId = this.availableNews.indexOf(currentNews);
            if (currentNewsId == 0) {
                if (actionEvent != null && actionEvent.getSource() instanceof Button) {
                    ((Button) actionEvent.getSource()).setDisable(true);
                }
                return;
            } else {
                currentNewsIndex = currentNewsId - 1;
            }

        } else {
            currentNewsIndex = 0;
        }
        log.info("Current page counter: {}", currentNewsIndex);
        NewsModel newsModel = getSelectedNewsModel();
        currentNews = newsModel;
        String fullHtml = getNewsAsHtml(newsModel);
        setNewsNameLabel(newsModel.getNewsResourceName());
        mainWebView.getEngine().loadContent(fullHtml, "text/html");
        if (nextNewsButton.isDisabled()) {
            nextNewsButton.setDisable(false);
        }
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            if (currentNewsIndex != 0) {
                ((Button) actionEvent.getSource()).setDisable(false);
            }

        }
    }

    private void showNextNews(ActionEvent actionEvent) {
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            ((Button) actionEvent.getSource()).setDisable(true);
        }
        if (currentNews != null) {
            int currentNewsId = this.availableNews.indexOf(currentNews);
            if (currentNewsId + 1 >= availableNews.size()) {
                if (actionEvent != null && actionEvent.getSource() instanceof Button) {
                    ((Button) actionEvent.getSource()).setDisable(true);
                }
                return;
            } else {
                currentNewsIndex = currentNewsId + 1;
            }

        } else {
            currentNewsIndex = 0;
        }
        if (previousNewsButton.isDisabled()) {
            previousNewsButton.setDisable(false);
        }
        log.info("Current page counter: {}", currentNewsIndex);
        NewsModel newsModel = getSelectedNewsModel();
        currentNews = newsModel;
        String fullHtml = getNewsAsHtml(newsModel);
        setNewsNameLabel(newsModel.getNewsResourceName());
        mainWebView.getEngine().loadContent(fullHtml, "text/html");
        if (actionEvent != null && actionEvent.getSource() instanceof Button) {
            if (currentNewsIndex + 1 != availableNews.size()) {
                ((Button) actionEvent.getSource()).setDisable(false);
            }
        }
    }

    private void reloadNews(String timePeriod) {
        Task<Void> updateTask = new Task<>() {
            @Override
            protected Void call() {
                newsService.checkForUpdates();
                return null;
            }
        };
        updateTask.setOnSucceeded(evt -> {
            log.info("News updated!");
            List<NewsModel> newsModelList;
            if (timePeriod == null || timePeriod.isEmpty() || timePeriod.equals("all")) {
                newsModelList = newsService.getAllNews();
            } else {
                newsModelList = newsService.getNewsForTimePeriod(timePeriod);
            }
            if (newsModelList != null && newsModelList.size() > 0) {
                log.info("There is some news...");
                availableNews.clear();
                availableNews.addAll(newsModelList);
                log.info("News loaded and added!");
                resetCounters();
                showNextNews(null);
            } else {
                log.warn("No news to show");
                noNewsPage();
            }
        });

        updateTask.setOnFailed(evt -> {
            log.error("News not updated!");
        });
        executor.execute(new Thread(updateTask));
    }

    private void noNewsPage() {
        String noNewsString = "<div style=\"width=100%; height=100%;\">" +
                                    "<div style=\"width: 80%;height: auto;margin: 0 auto;padding: 10px;position: relative;\">" +
                                        "<h1 style=\"text-align=center;width=50%;margin: 0 auto;\"> NO NEWS LOADED </h1>" +
                                    "</div>" +
                                "</div>";
        mainWebView.getEngine().loadContent(noNewsString, "text/html");
    }

    private void resetCounters() {
        currentNews = null;
        currentNewsIndex = 0;
        previousNewsButton.setDisable(true);
        nextNewsButton.setDisable(false);
    }
}
