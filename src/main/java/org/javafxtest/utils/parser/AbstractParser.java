package org.javafxtest.utils.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.javafxtest.model.NewsModel;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Nikolay Boyko
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractParser implements INewsParser<NewsModel, String> {

    private final String parserName;
    private final String resourceUrl;
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public List<NewsModel> parseNewsResource() {
        return parseNewsResource(resourceUrl);
    }

    @Override
    public String getResourceName() {
        return parserName;
    }
}
