package org.javafxtest.model.impl;

import lombok.Getter;
import lombok.Setter;
import org.javafxtest.model.TextData;
import org.jsoup.parser.Tag;

@Getter
@Setter
public class DivTextData extends TextData {

    public DivTextData(Tag dataTag) {
        super(dataTag);
    }

    @Override
    public String getHtmlString() {
        if (getChildrenTextData() != null && getChildrenTextData().size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("<").append(getTextDataType()).append(">");
            for (TextData textData : getChildrenTextData()) {
                builder.append(textData.getHtmlString());
            }
            builder.append("</").append(getTextDataType()).append(">");
            return builder.toString();
        } else {
            return "<div/>";
        }
    }
}
