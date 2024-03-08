package org.javafxtest.model;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.parser.Tag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
public class NewsModel {

    private String newsResourceName;
    private String newsHeadline;
    private String newsDescription;
    private String urlMediaSource;
    private Tag sourceType;
    private List<String> newsTextData;
    private TextData newsData;
    private LocalDateTime publicationTime;

    public String getDivMediaSource() {
        return this.urlMediaSource != null ?
                "<div>" + "<" + this.sourceType + " style=\"max-width: 100%;max-height: 100vh;height: auto;\""
                        + " src=\"" + this.urlMediaSource + "\"/></div>" : "";
    }

    public String getHeadlineAsHtml() {
        return this.newsHeadline != null ?
                "<div><h2>" + this.newsHeadline + "</h2></div>" : "";
    }

    public String getDescriptionAsHtml() {
        return this.newsDescription != null ?
                "<div><i>" + this.newsDescription + "</i></div>" : "";
    }

    public String getNewsPublicationAsHtml() {
        if (this.publicationTime != null) {

            String text = this.publicationTime.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
            return "<div style=\"margin-left: auto; margin-right: 0;\"><i>" + text + "</i></div>";
        } else {
            return "";
        }
    }
}
