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
public class HTextData extends TextData {

    public HTextData(Tag dataType) {
        super(dataType);
    }

    @Override
    public String getHtmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(getTextDataType()).append(">");
        builder.append(getText());
        builder.append("</").append(getTextDataType()).append(">");
        return builder.toString();
    }
}
