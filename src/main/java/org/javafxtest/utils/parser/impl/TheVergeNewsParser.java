package org.javafxtest.utils.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.model.NewsModel;
import org.javafxtest.utils.parser.AbstractParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            for (Element element : articleComponents.children()) {
                Object returnedData = elementProcessor(element);
                if (returnedData instanceof String) {
                    if (!((String) returnedData).isBlank()) {
                        listOfText.add((String) returnedData);
                    }
                }
            }
            NewsModel newsModel = new NewsModel();
            newsModel.setNewsResourceName(getResourceName());
            newsModel.setNewsDescription(newsDescription);
            newsModel.setNewsHeadline(newsHeadline);
            newsModel.setNewsTextData(listOfText);
            newsModel.setPublicationTime(date);
            return newsModel;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
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
                        String textFromElement = (String) elementProcessor((Element) childrenElement);
                        if (textFromElement != null && !textFromElement.isBlank()) {
                            textBuilderForP.append(textFromElement);
                        }
                    }
                }
                return textBuilderForP.toString();
//                StringBuilder collectedText = new StringBuilder();
//                for (Node textNodes : element.childNodes()) {
//                    if (textNodes instanceof TextNode) {
//                        collectedText.append(" ");
//                        collectedText.append(((TextNode) textNodes).text().trim());
//                    }
//                }
//                return collectedText.toString();
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

            case "a":
                String textToReturn = null;
                for (Node nodes : element.childNodes()) {
                    if (nodes instanceof TextNode) {
                        textToReturn = ((TextNode) nodes).text();
                        String collectedHref = element.attr("href");
                        if (!collectedHref.startsWith("https://")) {
                            collectedHref = getResourceUrl() + collectedHref;
                        }
                        textToReturn = textToReturn + " (" + collectedHref + ") ";
                    } else if (nodes instanceof Element) {
                        if (nodes.childNodes().size() > 1) {
                            for (Element nodesElement : ((Element) nodes).children()) {
                                textToReturn += elementProcessor(nodesElement);
                            }
                        } else if (nodes.childNodes().size() == 1) {
                            if (nodes.childNode(0) instanceof TextNode) {
                                textToReturn += ((TextNode) nodes.childNode(0)).text();
                            } else {
                                textToReturn += elementProcessor((Element) nodes.childNode(0));
                            }
                        }
                    }
                }
                return textToReturn;
            default:
                log.warn("What is this tag? {}", tag);
                break;
        }
        return null;
    }
}
