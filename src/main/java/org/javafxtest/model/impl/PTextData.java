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
        StringBuffer buffer = new StringBuffer();
        buffer.append("<").append(getTextDataType()).append(">");
        if (getText() != null && !getText().isEmpty()) {
            buffer.append(getText());
        }
        if (getChildrenTextData().size() > 0) {
            for (TextData data : getChildrenTextData()) {
                buffer.append(data.getHtmlString());
            }
            buffer.append("</").append(getTextDataType()).append(">");
        } else {
            buffer.append("</>");
        }
        return buffer.toString();
    }
}
