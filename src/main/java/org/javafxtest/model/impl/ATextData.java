package org.javafxtest.model.impl;

import lombok.Getter;
import lombok.Setter;
import org.javafxtest.model.TextData;
import org.jsoup.parser.Tag;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
public class ATextData extends TextData {

    private String href;

    public ATextData(Tag dataType) {
        super(dataType);
    }

    @Override
    public String getHtmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(getTextDataType());
        if (getHref() != null && !getHref().isEmpty()) {
            builder.append(" href=\"").append(getHref()).append("\"");
            builder.append(">").append(getText()).append("</").append(getTextDataType()).append(">");
        } else {
            builder.append("/>");
        }
        return builder.toString();
    }
}
