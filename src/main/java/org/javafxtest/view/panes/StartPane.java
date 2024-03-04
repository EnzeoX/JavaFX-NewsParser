package org.javafxtest.view.panes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.javafxtest.controller.gui.StartMenuController;
import org.springframework.stereotype.Component;

/**
 * @author Nikolay Boyko
 */

@Slf4j
public class StartPane extends GridPane {

//    private final StartMenuController controller;

    //elements
    private WebView webView;
    private AnchorPane headerPane;
    private AnchorPane bodyPane;
    private Label headerLabel;
    private Button loadButton;
    private Button settingsButton;

    private final String webElement = "<html>" +
            "<div>" +
                "<div class=\"duet--article--article-body-component\">" +
                    "<p class=\"duet--article--dangerously-set-cms-markup duet--article--standard-paragraph mb-20 font-fkroman text-18 leading-160 -tracking-1 selection:bg-franklin-20 dark:text-white dark:selection:bg-blurple [&amp;_a:hover]:shadow-highlight-franklin dark:[&amp;_a:hover]:shadow-highlight-blurple [&amp;_a]:shadow-underline-black dark:[&amp;_a]:shadow-underline-white\">Google Drive for iOS now lets you filter searches using dropdown menus for File Type, Owners, and Last Modified, the company <a href=\"https://workspaceupdates.googleblog.com/2024/02/release-notes-03-01-2024.html\">wrote on Friday in its Workspace Updates blog</a>. The dropdown menus show up before and after a search, and relevant filter recommendations will show up as well as users type.</p>" +
                 "</div>" +
                "<div class=\"duet--article--article-body-component\">" +
                    "<p class=\"duet--article--dangerously-set-cms-markup duet--article--standard-paragraph mb-20 font-fkroman text-18 leading-160 -tracking-1 selection:bg-franklin-20 dark:text-white dark:selection:bg-blurple [&amp;_a:hover]:shadow-highlight-franklin dark:[&amp;_a:hover]:shadow-highlight-blurple [&amp;_a]:shadow-underline-black dark:[&amp;_a]:shadow-underline-white\">Google says the update is available to Google Workspace customers and individual subscribers, as well as anyone with a personal Google account on iOS. The feature hasn’t been rolled out for Android users yet, but Google says that’s coming, too. </p>" +
                "</div>" +
                "<div class=\"duet--article--article-body-component\">" +
                    "<p class=\"duet--article--dangerously-set-cms-markup duet--article--standard-paragraph mb-20 font-fkroman text-18 leading-160 -tracking-1 selection:bg-franklin-20 dark:text-white dark:selection:bg-blurple [&amp;_a:hover]:shadow-highlight-franklin dark:[&amp;_a:hover]:shadow-highlight-blurple [&amp;_a]:shadow-underline-black dark:[&amp;_a]:shadow-underline-white\">Here’s a screen recording I made to show how it works:</p>" +
                "</div>" +
//                "<div class=\"duet--article--article-body-component clear-both block\">" +
//                    "<div class=\"my-9\"><div class=\"transition-all duration-300 ease-in-out\"><div class=\"fixed inset-0 h-[110vh] w-full bg-white transition-all duration-300 ease-in-out z-[-1] cursor-default opacity-0\" role=\"button\" aria-label=\"Close\" tabindex=\"0\">" +
//                    "</div>" +
//                    "<div role=\"button\" aria-label=\"Zoom\" tabindex=\"0\" class=\"visible z-30 w-full origin-center transition-all duration-300 ease-in-out cursor-zoom-in\">" +
//                        "<figure class=\"transition-all duration-300 ease-in-out lg:mx-0\">" +
//                            "<div>" +
//                                "<div class=\"duet--media--content-warning relative\" style=\"padding-top:216.86746987951807%\">" +
//                                    "<span style=\"box-sizing:border-box;display:block;overflow:hidden;width:initial;height:initial;background:none;opacity:1;border:0;margin:0;padding:0;position:absolute;top:0;left:0;bottom:0;right:0\">" +
//                                        "<img alt=\"A GIF showing a search filtered by images, and then by a date range.\" src=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif\" decoding=\"async\" data-nimg=\"fill\" style=\"position:absolute;top:0;left:0;bottom:0;right:0;box-sizing:border-box;padding:0;border:none;margin:auto;display:block;width:0;height:0;min-width:100%;max-width:100%;min-height:100%;max-height:100%;object-fit:cover\" sizes=\"(max-width: 1023px) 100vw, 744px\" srcset=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/376x815/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 376w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/384x833/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 384w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/415x900/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 415w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/480x1041/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 480w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/540x1171/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 540w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/640x1388/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 640w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/750x1627/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 750w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/828x1796/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 828w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1080x2342/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1080w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1200x2602/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1200w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1440x3123/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1440w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1920x4164/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1920w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2048x4441/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2048w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2400w\">" +
//                                        "<noscript>" +
//                                            "<img alt=\"A GIF showing a search filtered by images, and then by a date range.\" loading=\"lazy\" decoding=\"async\" data-nimg=\"fill\" style=\"position:absolute;top:0;left:0;bottom:0;right:0;box-sizing:border-box;padding:0;border:none;margin:auto;display:block;width:0;height:0;min-width:100%;max-width:100%;min-height:100%;max-height:100%;object-fit:cover\" sizes=\"(max-width: 1023px) 100vw, 744px\" srcSet=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/376x815/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 376w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/384x833/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 384w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/415x900/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 415w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/480x1041/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 480w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/540x1171/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 540w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/640x1388/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 640w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/750x1627/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 750w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/828x1796/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 828w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1080x2342/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1080w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1200x2602/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1200w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1440x3123/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1440w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1920x4164/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1920w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2048x4441/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2048w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2400w\" src=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif\"/>" +
//                                        "</noscript>" +
//                                    "</span>" +
//                                "</div>" +
//                            "</div>" +
//                        "</figure>" +
//                    "</div>" +
//                    "<div class=\"z-1 w-full hidden\">" +
//                        "<figure class=\"transition-all duration-300 ease-in-out lg:mx-0\"><div><div class=\"duet--media--content-warning relative\" style=\"padding-top:216.86746987951807%\">" +
//                            "<span style=\"box-sizing:border-box;display:block;overflow:hidden;width:initial;height:initial;background:none;opacity:1;border:0;margin:0;padding:0;position:absolute;top:0;left:0;bottom:0;right:0\">" +
//                                "<img alt=\"A GIF showing a search filtered by images, and then by a date range.\" src=\"data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7\" decoding=\"async\" data-nimg=\"fill\" style=\"position:absolute;top:0;left:0;bottom:0;right:0;box-sizing:border-box;padding:0;border:none;margin:auto;display:block;width:0;height:0;min-width:100%;max-width:100%;min-height:100%;max-height:100%;object-fit:cover\">" +
//                                "<noscript>" +
//                                    "<img alt=\"A GIF showing a search filtered by images, and then by a date range.\" loading=\"lazy\" decoding=\"async\" data-nimg=\"fill\" style=\"position:absolute;top:0;left:0;bottom:0;right:0;box-sizing:border-box;padding:0;border:none;margin:auto;display:block;width:0;height:0;min-width:100%;max-width:100%;min-height:100%;max-height:100%;object-fit:cover\" sizes=\"(max-width: 1023px) 100vw, 744px\" srcSet=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/376x815/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 376w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/384x833/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 384w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/415x900/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 415w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/480x1041/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 480w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/540x1171/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 540w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/640x1388/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 640w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/750x1627/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 750w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/828x1796/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 828w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1080x2342/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1080w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1200x2602/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1200w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1440x3123/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1440w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/1920x4164/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 1920w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2048x4441/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2048w, https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif 2400w\" src=\"https://duet-cdn.vox-cdn.com/thumbor/0x0:498x1080/2400x5205/filters:focal(249x540:250x541):no_upscale():format(webp)/cdn.vox-cdn.com/uploads/chorus_asset/file/25316492/Google_Drive_search_filter_example.gif\"/>" +
//                                "</noscript>"+
//                            "</span>" +
//                        "</div>" +
//                    "</div>" +
//                        "</figure>" +
//                    "</div>" +
//                "</div>" +
                "<div class=\"duet--media--caption pt-6 font-polysans-mono text-12 font-light leading-130 tracking-1\">" +
                    "<figcaption class=\"duet--article--dangerously-set-cms-markup inline text-gray-13 dark:text-gray-e9 [&amp;>a:hover]:text-black [&amp;>a:hover]:shadow-underline-black dark:[&amp;>a:hover]:text-gray-e9 dark:[&amp;>a:hover]:shadow-underline-gray-63 [&amp;>a]:shadow-underline-gray-13 dark:[&amp;>a]:shadow-underline-gray-63\">" +
                        "<em>Dropdown menus are GOAT, as the youths say.</em>" +
                    "</figcaption>" +
                    "<cite class=\"duet--article--dangerously-set-cms-markup inline not-italic text-gray-63 dark:text-gray-bd [&amp;>a:hover]:text-gray-63 [&amp;>a:hover]:shadow-underline-black dark:[&amp;>a:hover]:text-gray-bd dark:[&amp;>a:hover]:shadow-underline-gray [&amp;>a]:shadow-underline-gray-63 dark:[&amp;>a]:text-gray-bd dark:[&amp;>a]:shadow-underline-gray\">" +
                        "Screen recording: Wes Davis / The Verge" +
                    "</cite>" +
                "</div>" +
            "</div>" +
            "</div>" +
            "<div class=\"duet--article--article-body-component\"><p class=\"duet--article--dangerously-set-cms-markup duet--article--standard-paragraph mb-20 font-fkroman text-18 leading-160 -tracking-1 selection:bg-franklin-20 dark:text-white dark:selection:bg-blurple [&amp;_a:hover]:shadow-highlight-franklin dark:[&amp;_a:hover]:shadow-highlight-blurple [&amp;_a]:shadow-underline-black dark:[&amp;_a]:shadow-underline-white\">The new filter update makes it much more pleasant to search and browse for files when you don’t know where they are. For instance, if you know you’re looking for a video stored in Drive but have no idea what it’s called, you can just tap the Videos file type, then choose “custom range” under the last modified and search for the time period you last touched it. I’d love to see this updated to let me filter by other types of dates or file sizes.</p></div><div class=\"duet--article--article-body-component\"><p class=\"duet--article--dangerously-set-cms-markup duet--article--standard-paragraph mb-20 font-fkroman text-18 leading-160 -tracking-1 selection:bg-franklin-20 dark:text-white dark:selection:bg-blurple [&amp;_a:hover]:shadow-highlight-franklin dark:[&amp;_a:hover]:shadow-highlight-blurple [&amp;_a]:shadow-underline-black dark:[&amp;_a]:shadow-underline-white\">It’s a marked improvement to Google’s previous approach, which involved filter suggestions and swipeable filters. Turns out, dropdown menus are still the best. Who’d have thought?</p></div><div class=\"duet--article--article-body-component\"></div></div></html>";
////    public StartPane(StartMenuController controller) {
////        this.controller = controller;
//        init();
//    }

    public StartPane() {
        init();
    }

    private void init() {
        webView = new WebView();
        bodyPane = new AnchorPane();
        headerPane = new AnchorPane();
        headerLabel = new Label();
        loadButton = new Button();
        settingsButton = new Button();
        configureElements();
    }

    private void configureElements() {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setMinWidth(10);
        columnConstraints.setPrefWidth(100);
        columnConstraints.setMaxWidth(USE_COMPUTED_SIZE);

        this.getColumnConstraints().add(columnConstraints);

        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            if (i == 0) {
                rowConstraints.setPrefHeight(75);
                rowConstraints.setMaxHeight(75);
                rowConstraints.setMinHeight(75);
            } else {
                rowConstraints.setPrefHeight(325);
                rowConstraints.setMaxHeight(75);
                rowConstraints.setMinHeight(USE_COMPUTED_SIZE);
            }
            this.getRowConstraints().add(rowConstraints);
        }

        headerPane.setMinWidth(USE_PREF_SIZE);
        headerPane.setMinHeight(USE_PREF_SIZE);
        headerPane.setPrefWidth(600);
        headerPane.setPrefHeight(45);
        headerPane.setMaxHeight(USE_COMPUTED_SIZE);
        headerPane.setMaxWidth(USE_COMPUTED_SIZE);
        headerPane.setStyle("-fx-background-color: #443996");
        GridPane.setConstraints(headerPane, 0,0);
        this.getChildren().add(headerPane);

        bodyPane.setMinWidth(USE_COMPUTED_SIZE);
        bodyPane.setMinHeight(USE_COMPUTED_SIZE);
        bodyPane.setPrefWidth(200);
        bodyPane.setPrefHeight(200);
        bodyPane.setMaxHeight(USE_COMPUTED_SIZE);
        bodyPane.setMaxWidth(USE_COMPUTED_SIZE);
        GridPane.setConstraints(bodyPane, 0,1);
        this.getChildren().add(bodyPane);

        headerLabel.setMinWidth(USE_COMPUTED_SIZE);
        headerLabel.setMinHeight(USE_COMPUTED_SIZE);
        headerLabel.setPrefWidth(600);
        headerLabel.setPrefHeight(45);
        headerLabel.setMaxHeight(USE_PREF_SIZE);
        headerLabel.setMaxWidth(USE_PREF_SIZE);
        headerLabel.setText("NEWS VIEWER");
        headerLabel.setFont(new Font("Bauhaus 93", 40));
        headerLabel.setTextFill(Paint.valueOf("#d7d6d6"));
        headerPane.getChildren().add(headerLabel);

        loadButton.setMinWidth(USE_COMPUTED_SIZE);
        loadButton.setMinHeight(USE_COMPUTED_SIZE);
        loadButton.setPrefWidth(120);
        loadButton.setPrefHeight(10);
        loadButton.setMaxHeight(USE_COMPUTED_SIZE);
        loadButton.setMaxWidth(USE_COMPUTED_SIZE);
        loadButton.setLayoutX(242);
        loadButton.setLayoutY(41);
        loadButton.setText("LOAD");
        loadButton.setFont(new Font("Bauhaus 93", 18));
        loadButton.setTextFill(Paint.valueOf("#443996"));
        loadButton.setTextAlignment(TextAlignment.CENTER);
        loadButton.setStyle("-fx-background-color: lightgray; -fx-background-radius:20");
        loadButton.setId("main_start_button");

        settingsButton.setMinWidth(USE_COMPUTED_SIZE);
        settingsButton.setMinHeight(USE_COMPUTED_SIZE);
        settingsButton.setPrefWidth(120);
        settingsButton.setPrefHeight(10);
        settingsButton.setMaxHeight(USE_COMPUTED_SIZE);
        settingsButton.setMaxWidth(USE_COMPUTED_SIZE);
        settingsButton.setLayoutX(242);
        settingsButton.setLayoutY(83);
        settingsButton.setText("LOAD");
        settingsButton.setFont(new Font("Bauhaus 93", 18));
        settingsButton.setTextFill(Paint.valueOf("#443996"));
        settingsButton.setTextAlignment(TextAlignment.CENTER);
        settingsButton.setStyle("-fx-background-color: lightgray; -fx-background-radius:20");
        settingsButton.setId("main_settings_button");
        bodyPane.getChildren().addAll(loadButton, settingsButton);

        webView.setMinWidth(0);
        webView.setMinHeight(0);
        webView.setPrefWidth(575);
        webView.setPrefHeight(500);
        webView.setMaxHeight(Long.MAX_VALUE);
        webView.setMaxWidth(Long.MAX_VALUE);
        webView.setLayoutX(14);
        webView.setLayoutY(125);
        webView.setId("web_viewer_object");
        webView.getEngine().loadContent(webElement, "text/html");
        bodyPane.getChildren().add(webView);
    }

}
