package org.javafxtest.controller.gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
public class StartMenuController {

    private final HostServices hostServices;

    private final ApplicationContext applicationContext;

    @FXML
    private GridPane mainGridPane;

    @FXML
    private AnchorPane anchorPaneBody;

    @FXML
    private AnchorPane anchorPaneHeader;

    @FXML
    private Button mainLoadButton;

    @FXML
    private Button mainSettingsButton;

    @FXML
    private Label newsLabel;

    public StartMenuController(HostServices hostServices,
                               ApplicationContext applicationContext) {
        this.hostServices = hostServices;
        this.applicationContext = applicationContext;
    }

    @FXML
    void initialize() {
        mainLoadButton.setOnMouseClicked(this::loadNewsMenu);
    }

    private void loadNewsMenu(MouseEvent actionEvent) {
        Stage stage;
        try {
            stage = (Stage) mainLoadButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/news_menu.fxml"));
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
}
