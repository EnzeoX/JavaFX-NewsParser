package org.javafxtest;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class FxTestApplication {

    public static void main(String[] args) { Application.launch(FxRunner.class, args);
    }
}
