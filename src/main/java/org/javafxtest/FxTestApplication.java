package org.javafxtest;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FxTestApplication {

    public static void main(String[] args) { Application.launch(FxRunner.class, args);
    }
}
