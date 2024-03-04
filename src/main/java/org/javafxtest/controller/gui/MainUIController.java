package org.javafxtest.controller.gui;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Component
public class MainUIController {

    private final HostServices hostServices;

    private final ApplicationContext applicationContext;


    @FXML
    private Pane bodyPane;

    @FXML
    private Button bottomNextNewsButton;

    @FXML
    private Pane bottomPane;

    @FXML
    private Button bottomPreviousNewsButton;

    @FXML
    private Button headerBackToMenuButton;

    @FXML
    private Pane headerPane;

    @FXML
    private Label headerSelectTimePeriodLabel;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private GridPane mainGridPane;

    @FXML
    void initialize() {
        headerBackToMenuButton.setOnAction(this::backToMainMenu);
        bottomPreviousNewsButton.setOnAction(action -> {
        });
        bottomNextNewsButton.setOnAction(action -> {
        });
    }

    public MainUIController(HostServices hostServices,
                            ApplicationContext applicationContext) {
        this.hostServices = hostServices;
        this.applicationContext = applicationContext;
    }

    private void backToMainMenu(ActionEvent actionEvent) {
        Stage stage;
        try {
            Scene currentScene = headerBackToMenuButton.getScene();
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

    private void nextNews() {

    }
}
