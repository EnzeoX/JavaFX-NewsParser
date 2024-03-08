package org.javafxtest.listeners;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.javafxtest.FxRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Component
public class StageListener implements ApplicationListener<FxRunner.StageReadyEvent> {

    private final String applicationTitle;

    private final Resource fxml;
    private final ApplicationContext applicationContext;

    public StageListener(@Value("${application.ui.title}") String applicationTitle,
                         @Value("${classpath:/start.fxml}") Resource resource,
                         ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.applicationTitle = applicationTitle;
        this.fxml = resource;
    }

    @Override
    public void onApplicationEvent(FxRunner.StageReadyEvent event) {
        Stage stage;
        try {
            stage = event.getStage();
            URL url = this.fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setControllerFactory(this.applicationContext::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle(this.applicationTitle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
