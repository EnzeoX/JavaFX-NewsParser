package org.javafxtest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.parser.Tag;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Getter
@Setter
@NoArgsConstructor
public class TextData {

    public TextData(Tag dataType) {
        this.textDataType = dataType;
    }

    private Tag textDataType;

    private String text;



    private List<TextData> childrenTextData = new LinkedList<>();

    private TextData nextElement = null;

    private TextData previousElement = this;

    private TextData firstElement = null;

    private TextData lastElement = null;


    public void addTextData(TextData data) {
        if (this.firstElement == null) {
            this.firstElement = data;
        }
        this.childrenTextData.add(data);
        if (this.lastElement == null || !this.lastElement.equals(data)) {
            this.lastElement = data;
        }
    }

    public String getHtmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(this.textDataType).append(">");
        if (this.childrenTextData.size() > 0) {
            for (TextData child : this.childrenTextData) {
                builder.append(child.getHtmlString());
            }
            builder.append("</").append(this.textDataType).append(">");
        } else {
            builder.append("/>");
        }
        return builder.toString();
    }
}
