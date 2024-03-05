package org.javafxtest.utils.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.model.NewsModel;
import org.javafxtest.model.TextData;
import org.javafxtest.model.impl.*;
import org.javafxtest.utils.parser.AbstractParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Text;

import javax.swing.text.html.parser.TagElement;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
public class TheVergeNewsParser extends AbstractParser {

    @SuppressWarnings("all")
    public TheVergeNewsParser(@Value("${news-resource.theverge.url}") String resourceUrl,
                              @Value("${news-resource.theverge.name}") String resourceName) {
        super(resourceName, resourceUrl);
    }

    @Override
    public List<NewsModel> parseNewsResource(String resource) {
        List<String> newsPages = getNewsPagesUrls(resource);
        if (newsPages.size() == 0) {
            log.warn("No news pages found");
            return new LinkedList<>();
        }
        return processPages(newsPages);
    }

    private List<String> getNewsPagesUrls(String resource) {
        Objects.requireNonNull(resource, "No resource provided for parse");
        try {
            Document doc = Jsoup.connect(resource).get();
            Elements elements = doc.select("div.duet--content-cards--content-card.group");
            List<String> urls = new ArrayList<>();
            for (Element element : elements) {
                String dateStr = null;
                try {
                    dateStr = element.childNodes()
                            .get(0).childNodes()
                            .get(1).childNodes()
                            .get(2).childNodes()
                            .get(1).childNodes()
                            .get(0).attr("datetime"); // gate datetime of news
                } catch (IndexOutOfBoundsException ie) {
                    log.error(ie.getMessage());
                }
                if (dateStr == null || dateStr.isBlank()) {
                    continue;
                }
                LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
                if (LocalDateTime.now().getDayOfMonth() != date.getDayOfMonth()) { // exclude news if they are old
                    log.warn("Processed date is not \"today\"");
                    continue;
                }
                Element elem = element
                        .select("h2.font-polysans.text-20.font-bold.tracking-1")
                        .first();
                if (elem == null) {
                    log.warn("Element for query  \"h2.font-polysans.text-20.font-bold.tracking-1\" not found");
                    continue;
                }
                String newsPageUrl = elem.childNodes().get(0).attr("href");
                if (!newsPageUrl.isBlank() && newsPageUrl.length() > 1) {
                    urls.add(newsPageUrl);
                }
            }
            log.info("Total news pages URLs found: {}", urls.size());
            return urls;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new LinkedList<>();
        }
    }

    public List<NewsModel> processPages(List<String> pageUrls) {
        List<NewsModel> newsModelList = new LinkedList<>();
        for (String pageUrl : pageUrls) {
            NewsModel newsModel = processPage(pageUrl);
            if (newsModel != null) {
                newsModelList.add(newsModel);
            }
        }
        return newsModelList;
    }

    public NewsModel processPage(String pageUrl) {
        try {
            Document doc = Jsoup.connect(getResourceUrl() + pageUrl).get();
            Elements article = doc.select("article#content.mx-auto.my-24.w-full.max-w-container-lg");
            String newsHeadline = article.select("h1.mb-28.font-polysans.text-45.font-bold.leading-100").text(); // get news headline
            String newsDescription = article.select("span.font-polysans.text-22.font-light.leading-110").select("h2").text(); // get news description
            String publishedTime = article.select("div.duet--article--date-and-comments.mb-20.inline-block.font-polysans.text-12.text-gray-5a") // get news published time
                    .get(0).children()
                    .attr("datetime").trim();
            LocalDateTime date = LocalDateTime.parse(publishedTime, formatter);
            Element articleComponentContainer = article.select("div.duet--article--article-body-component-container.clearfix").first();
            Objects.requireNonNull(articleComponentContainer, "Article component container not found or null!");
            Element articleComponents = articleComponentContainer.child(0);
            List<String> listOfText = new LinkedList<>();
            TextData mainTextData = new TextData(Tag.valueOf("div"));
            for (Element element : articleComponents.children()) {
//                Object returnedData = elementProcessor(element);
//                if (returnedData instanceof String) {
//                    if (!((String) returnedData).isBlank()) {
//                        listOfText.add((String) returnedData);
//                    }
//                }
                TextData returnedData = processElements(element);
                if (returnedData != null) {
                    mainTextData.addTextData(returnedData);
                }
            }
            NewsModel newsModel = new NewsModel();
            newsModel.setNewsResourceName(getResourceName());
            newsModel.setNewsDescription(newsDescription);
            newsModel.setNewsHeadline(newsHeadline);
//            newsModel.setNewsTextData(listOfText);
            newsModel.setNewsData(mainTextData);
            newsModel.setPublicationTime(date);
            return newsModel;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private TextData processElements(Element element) {
        String tag = element.tag().getName();
        switch (tag) {
            case "div":
                if (element.attr("class").startsWith("duet--article--article-body-component clear-both block")) {
                    log.info("Skipping duet--article--article-body-component clear-both block");
                    return null;
                }
                TextData divTextData = new TextData(element.tag());
                if (element.attr("class").equals("duet--article--article-body-component")) {
                    log.info("div element is element of content container, processing");
                    for (Element elem : element.children()) {
                        divTextData.addTextData(processElements(elem));
                    }
                }
                return divTextData;
            case "p":
                PTextData pTextData = new PTextData(element.tag());
                List<Node> children = element.childNodes();
                for (Node childrenElement : children) {
                    if (childrenElement instanceof TextNode) {
                        TextData pChildData = new TextData(null);
                        pChildData.setText(((TextNode) childrenElement).text());
                        pTextData.addTextData(pChildData);
                    } else if (childrenElement instanceof Element) {
                        TextData dataFromElement = processElements((Element) childrenElement);
                        if (dataFromElement != null) {
                            pTextData.addTextData(dataFromElement);
                        }
                    }
                }
                return pTextData;
            case "em" :
                EmTextData emTextData = new EmTextData(element.tag());
                emTextData.setText(element.text());
                return emTextData;
            case "ul":
                Elements liElements = element.select("li.duet--article--dangerously-set-cms-markup.mb-16.pl-12");
                UlTextData ulTextData = new UlTextData(element.tag());
                for (Element liElement : liElements) {
                    ulTextData.addTextData(processElements(liElement));
                }
                return ulTextData;
            case "li":
                LiTextData liTextData = new LiTextData(element.tag());
                liTextData.setText(element.text());
                for (Element liChild : element.children()) {
                    liTextData.addTextData(processElements(liChild));
                }
                return liTextData;
            case "h3":
            case "h2":
            case "h1":
                HTextData hTextData = new HTextData(element.tag());
                for (Node hNode : element.children()) {
                    if (hNode instanceof TextNode) {
                        hTextData.setText(((TextNode) hNode).text());
                    } else if (hNode instanceof Element) {
                        hTextData.addTextData(processElements((Element) hNode));
                    }
                }
                return hTextData;
            case "a":
                ATextData aTextData = new ATextData(element.tag());
                for (Node node : element.childNodes()) {
                    if (node instanceof TextNode) {
                        aTextData.setText(((TextNode) node).text());
                        aTextData.setHref(element.attr("href"));
                        return aTextData;
                    } else if (node instanceof Element) {
                        if (node.childNodes().size() > 1) {
                            for (Element nodesElement : ((Element) node).children()) {
                                aTextData.getChildrenTextData().add(processElements(nodesElement));
                            }
                        } else if (node.childNodes().size() == 1) {
                            if (node.childNode(0) instanceof TextNode) {
                                aTextData.setText(((TextNode) node.childNode(0)).text());
                            } else {
                                aTextData.getChildrenTextData().add(processElements((Element) node.childNode(0)));
                            }
                        }
                    }
                }
                return aTextData;
            default:
                log.warn("What is this tag? {}", tag);
                break;
        }
        return null;
    }

    private Object elementProcessor(Element element) {
        String tag = element.tag().getName();
        switch (tag) {
            case "div":
                if (element.attr("class").startsWith("duet--article--article-body-component clear-both block")) {
                    log.info("Skipping duet--article--article-body-component clear-both block");
                    return "";
                }
                StringBuilder divTextAppender = new StringBuilder();
                if (element.attr("class").equals("duet--article--article-body-component")) {
                    log.info("div element is element of content container, processing");
                    for (Element elem : element.children()) {
                        divTextAppender.append(elementProcessor(elem));
                    }
                }
                return divTextAppender.toString();
            case "p":
                StringBuilder textBuilderForP = new StringBuilder();
                List<Node> children = element.childNodes();
                for (Node childrenElement : children) {
                    if (childrenElement instanceof TextNode) {
                        textBuilderForP.append(((TextNode) childrenElement).text());
                    } else if (childrenElement instanceof Element) {
                        Object textFromElement = elementProcessor((Element) childrenElement);
                        if (textFromElement != null) {
                            if (textFromElement instanceof String) {
                                textBuilderForP.append(textFromElement);
                            }
                        }
                    }
                }
                return textBuilderForP.toString();
            case "em" :
                return element.text();
            case "ul":
                Elements liElements = element.select("li.duet--article--dangerously-set-cms-markup.mb-16.pl-12");
                StringBuilder liBuilderText = new StringBuilder();
                for (Element liElement : liElements) {
                    liBuilderText.append(liElement.text()).append("\n");
                    Elements aElements = liElement.select("a");
                    String collectedHref = aElements.attr("href");
                    if (!collectedHref.startsWith("https://")) {
                        liBuilderText.append(getResourceUrl().isBlank() ? "https://www.theverge.com" : getResourceUrl());
                    }
                    liBuilderText.append(collectedHref).append("\n\n");
                }
                return liBuilderText.toString();
            case "h3":
                StringBuilder h3Builder = new StringBuilder();
                h3Builder.append("<h3>");
                for (Node h3Node : element.children()) {
                    if (h3Node instanceof TextNode) {
                        h3Builder.append(((TextNode) h3Node).text());
                    } else if (h3Node instanceof Element) {
                        h3Builder.append((String) elementProcessor((Element) h3Node));
                    }
                }
                h3Builder.append("</h3>");
                return h3Builder.toString();
            case "a":
                StringBuilder textToReturn = new StringBuilder();
                for (Node nodes : element.childNodes()) {
                    if (nodes instanceof TextNode) {
                        textToReturn.append(((TextNode) nodes).text());
                        String collectedHref = element.attr("href");
                        if (!collectedHref.startsWith("https://")) {
                            collectedHref = getResourceUrl() + collectedHref;
                        }
                        textToReturn.append(" (").append(collectedHref).append(") ");
                    } else if (nodes instanceof Element) {
                        if (nodes.childNodes().size() > 1) {
                            for (Element nodesElement : ((Element) nodes).children()) {
                                textToReturn.append(elementProcessor(nodesElement));
                            }
                        } else if (nodes.childNodes().size() == 1) {
                            if (nodes.childNode(0) instanceof TextNode) {
                                textToReturn.append(((TextNode) nodes.childNode(0)).text());
                            } else {
                                textToReturn.append(elementProcessor((Element) nodes.childNode(0)));
                            }
                        }
                    }
                }
                return textToReturn.toString();
            default:
                log.warn("What is this tag? {}", tag);
                break;
        }
        return null;
    }
}
