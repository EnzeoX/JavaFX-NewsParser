package org.javafxtest.utils.parser;

import org.javafxtest.utils.IParserParameters;

import java.util.List;

/**
 * @author Nikolay Boyko
 */
public interface INewsParser<T, R> extends IParserParameters {

    List<T> parseNewsResource();
    List<T> parseNewsResource(R resource);

}
