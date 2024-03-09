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
            Elements elements = doc.select("div.duet--content-cards--content-card.group.relative.z-10.flex.flex-row.items-center.justify-between.border-b.border-gray-cc.bg-white.py-16.text-black.max-w-container-sm");
            List<String> urls = new ArrayList<>();
            for (Element element : elements) {
                String dateStr = element.select("div.inline-block.text-gray-63").select("time[datetime]").attr("datetime"); // get published time
                if (dateStr.isEmpty()) {
                    log.warn("No published time found");
                    continue;
                }
                LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
                LocalDateTime now = LocalDateTime.now();
                log.info("Today date and day: {}, {}; Date and day parsed from page: {}, {};",
                        now, now.getDayOfMonth(),
                        date, date.getDayOfMonth());
                if (now.getDayOfMonth() != date.getDayOfMonth()) { // exclude news if they are old
                    log.warn("Processed date is not \"today\"");
                    continue;
                }
                String newsPageUrl = element
                        .select("h2.font-polysans.text-20.font-bold.tracking-1")
                        .select("a").attr("href");
                if (!newsPageUrl.isEmpty() && newsPageUrl.length() > 1) {
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
            try {
                NewsModel newsModel = processPage(pageUrl);
                if (newsModel != null) {
                    newsModelList.add(newsModel);
                }
            } catch (Exception e) {
                log.error("Exception happened while processPage. Page url: {}, exception: {}, message: {}", pageUrl, e.getClass(), e.getMessage());
            }
        }
        return newsModelList;
    }

    public NewsModel processPage(String pageUrl) {
        try {
            log.info("Processing page: {}", pageUrl);
            Document doc = Jsoup.connect(getResourceUrl() + pageUrl).get();
            Elements article = doc.select("article#content.mx-auto.w-full.max-w-container-lg");
            String newsHeadline = article.select("h1.mb-28.font-polysans.text-45.font-bold.leading-100").text(); // get news headline
            if (newsHeadline.isEmpty()) {
                newsHeadline = article.select("header#stream-lede.duet--article--lede.mx-auto").select("h1.duet--article--feature-headline.sticky-nav-trigger.mb-8").text();
            }
            Elements mediaResource = doc.select("figure.duet--article--lede-image.w-full");
            // try to find which type of
            String urlMediaResource = null;
            Tag mediaResourceType = null;
            if (!mediaResource.select("img").isEmpty()) {
                log.info("UrlMediaResource is img");
                mediaResourceType = Tag.valueOf("img");
                urlMediaResource = mediaResource.select("img").attr("src");
            } else if (!mediaResource.select("video").isEmpty()) {
                log.info("UrlMediaResource is video");
                mediaResourceType = Tag.valueOf("video");
                urlMediaResource = mediaResource.select("video").attr("src");
            }
            String newsDescription = article.select("span.font-polysans.text-22.font-light.leading-110").select("h2").text(); // get news description
            String publishedTime = doc.select("time[datetime]").attr("datetime"); // get published time
            LocalDateTime date = LocalDateTime.parse(publishedTime, formatter);
            Element articleComponentContainer = article.select("div.duet--article--article-body-component-container").first();
            Objects.requireNonNull(articleComponentContainer, "Article component container not found or null!");
            TextData mainTextData = new TextData(Tag.valueOf("div"));
            if (articleComponentContainer.children().size() > 1) {
                for (Element articleElement : articleComponentContainer.children()) {
                    if (articleElement.children().size() > 0) {
                        for (Element articleElementChild : articleElement.children()) {
                            TextData returnedData = processElements(articleElementChild);
                            if (returnedData != null) {
                                mainTextData.addTextData(returnedData);
                            }
                        }
                    }
                }
            } else {
                Element articleComponents = articleComponentContainer.child(0);

                for (Element element : articleComponents.children()) {
                    TextData returnedData = processElements(element);
                    if (returnedData != null) {
                        mainTextData.addTextData(returnedData);
                    }
                }
            }

            NewsModel newsModel = new NewsModel();
            if (urlMediaResource != null && !urlMediaResource.isEmpty()) {
                newsModel.setSourceType(mediaResourceType);
                newsModel.setUrlMediaSource(urlMediaResource);
            }
            newsModel.setNewsResourceName(getResourceName());
            newsModel.setNewsDescription(newsDescription);
            newsModel.setNewsHeadline(newsHeadline);
            newsModel.setNewsData(mainTextData);
            newsModel.setPublicationTime(date);
            return newsModel;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private TextData processElements(Element element) {
        try {
            String tag = element.tag().getName();
            switch (tag) {
                case "div":
                    if (element.attr("class").startsWith("duet--article--article-body-component clear-both block")) {
//                        log.info("Skipping duet--article--article-body-component clear-both block");
                        return null;
                    }
                    DivTextData divTextData = new DivTextData(element.tag());
                    if (element.attr("class").equals("duet--article--article-body-component")
                     || element.attr("class").equals("mt-30")) {
//                        log.info("div element is element of content container, processing");
                        for (Element elem : element.children()) {
                            TextData elementData = processElements(elem);
                            if (elementData == null) {
                                continue;
                            }
                            divTextData.addTextData(elementData);
                        }
                    }
                    return divTextData;
                case "selection":

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
                case "em":
                    EmTextData emTextData = new EmTextData(element.tag());
                    emTextData.setText(element.text());
                    return emTextData;
                case "ul":
                    Elements liElements = element.select("li.duet--article--dangerously-set-cms-markup.mb-16.pl-12");
                    if (liElements.size() == 0) {
                        liElements = element.select("role");
                    }
                    UlTextData ulTextData = new UlTextData(element.tag());
                    for (Element liElement : liElements) {
                        ulTextData.addTextData(processElements(liElement));
                    }
                    return ulTextData;
                case "li":
                    LiTextData liTextData = new LiTextData(element.tag());
                    if (!element.text().isEmpty()) {
                        liTextData.setText(element.text());
                    }
                    if (element.children().size() > 0) {
                        for (Element liChild : element.children()) {
                            liTextData.addTextData(processElements(liChild));
                        }
                    }
                    return liTextData;
                case "h4":
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
                case "strong":
                    StrongTextData strongTextData = new StrongTextData(element.tag());
                    strongTextData.setText(element.text());
                    return strongTextData;
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
                    log.warn("Skipping unknown tag? {}", tag);
                    break;
            }
            return null;
        } catch (Exception e) {
            log.error("Exception happened while processing element {}, error type {}, message: {}", element, e.getClass(), e.getMessage());
            return null;
        }
    }
}
