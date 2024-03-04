package org.javafxtest.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
public class NewsModel {

    private String newsResourceName;
    private String newsHeadline;
    private String newsDescription;
    private List<String> newsTextData;
    private LocalDateTime publicationTime;
}
