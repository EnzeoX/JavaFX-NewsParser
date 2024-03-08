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
public class EmTextData extends TextData {

    public EmTextData(Tag dataType) {
        super(dataType);
    }

    @Override
    public String getHtmlString() {
        if (getText() != null && !getText().isEmpty()) {
            return "<em>" + getText() + "</em> ";
        }
        return "<em/>";
    }
}
