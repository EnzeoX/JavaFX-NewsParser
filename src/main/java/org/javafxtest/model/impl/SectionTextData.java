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
public class SectionTextData extends TextData {

    public SectionTextData(Tag dataType) {
        super(dataType);
    }

    @Override
    public String getHtmlString() {
        StringBuilder builder = new StringBuilder();

        if (getChildrenTextData() != null && getChildrenTextData().size() > 0) {
            builder.append("<").append(getTextDataType()).append(">");
            for (TextData textData : getChildrenTextData()) {
                builder.append(textData.getHtmlString());
            }
            builder.append("</").append(getTextDataType()).append(">");
        } else {
            builder.append("<").append(getTextDataType()).append("/>");
        }
        return builder.toString();
    }
}
