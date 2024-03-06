package org.javafxtest;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.javafxtest.controller.gui.StartMenuController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Nikolay Boyko
 */
public class FxRunner extends Application {

    private ConfigurableApplicationContext context;

    private String applicationTitle = "";

    @Override
    public void init() {
        String[] parameters = getParameters().getRaw().toArray(new String[0]);
//        ApplicationContextInitializer<GenericApplicationContext> initializer =
//                applicationContext -> {
//                    applicationContext.registerBean(Application.class, () -> FxRunner.this);
//                    applicationContext.registerBean(Parameters.class, this::getParameters);
//                    applicationContext.registerBean(HostServices.class, this::getHostServices);
//                };
        this.context = new SpringApplicationBuilder()
                .sources(FxTestApplication.class)
//                .initializers(initializer)
                .run(parameters);
    }

    @Override
    public void start(Stage stage) {
//        this.context.publishEvent(new StageReadyEvent(stage)); FOR STAGE LISTENER
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(StartMenuController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setTitle(this.applicationTitle);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
//        this.context.stop();
        this.context.close();
        Platform.exit();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public Stage getStage() {
            return (Stage) getSource();
        }

        public StageReadyEvent(Object source) {
            super(source);
        }
    }
}
