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
public class PTextData extends TextData {

    public PTextData(Tag dataType) {
        super(dataType);
    }

    @Override
    public String getHtmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(getTextDataType()).append(">");
        if (getText() != null && !getText().isBlank()) {
            builder.append(getText());
        }
        if (getChildrenTextData().size() > 0) {
            for (TextData data : getChildrenTextData()) {
                builder.append(data.getHtmlString());
            }
            builder.append(builder.append("</").append(getTextDataType()).append(">"));
        } else {
            builder.append("</>");
        }
        return builder.toString();
    }
}
