package org.javafxtest.fxtest;

import lombok.extern.slf4j.Slf4j;
import org.javafxtest.entity.NewsEntity;
import org.javafxtest.entity.NewsTextData;
import org.javafxtest.model.NewsModel;
import org.javafxtest.model.TextData;
import org.javafxtest.utils.parser.impl.TheVergeNewsParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsoupTest {

    private String pageWithLi = "https://www.theverge.com/24087874/smart-kitchen-appliances-cooking-cleaning-vergecast";
    private String simplePage = "https://www.theverge.com/2024/3/3/24089196/apple-no-spring-event-planned-macbook-air-ipad";
    private String complexPage = "https://www.theverge.com/2024/3/4/24090828/oregon-right-to-repair-sb-1596-parts-pairing";
    private String pageWithH3 = "https://www.theverge.com/21280354/best-ipad-deals-apple";
    private String pageWithIndexError = "https://www.theverge.com/2024/3/5/24091719/microsoft-new-york-times-openai-motion-dismiss-copyright-lawsuit";
    private String pageDifferentStructure = "https://www.theverge.com/24083744/netflix-3-body-problem-trailers-review-interview";


    @InjectMocks
    private TheVergeNewsParser theVergeNewsParser;

    @Test
    public void testService_processPageUncommonStructure() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(pageDifferentStructure);
        log.info("Model loaded");
        log.info("HTML data:");
        String htmlString = model.getNewsData().getHtmlString();
        log.info(htmlString);
    }

    @Test
    public void testService_processPageIndexError() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(pageWithIndexError);
        log.info("Model loaded");
        log.info("HTML data:");
        String htmlString = model.getNewsData().getHtmlString();
        log.info(htmlString);
    }

    @Test
    public void testService_TheVergeParser() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "https://www.theverge.com");
        List<NewsModel> modelList = this.theVergeNewsParser.parseNewsResource(this.theVergeNewsParser.getResourceUrl());
    }

    @Test
    public void testService_TheVergeOnePageParseWithH3() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(pageWithH3);
        log.info("Model loaded");
        log.info("HTML data:");
        String htmlString = model.getNewsData().getHtmlString();
        log.info(htmlString);
    }

    @Test
    public void testService_TheVergeOnePageParseWithLi() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(pageWithLi);
        log.info(model.getNewsData().getHtmlString());
        log.info("Model loaded");
    }

    @Test
    public void testService_TheVergeOnePageParseSimple() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(simplePage);
        TextData textData = model.getNewsData();
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setNewsName("The Verge");
        newsEntity.setNewsDescription("The Description");
        newsEntity.setNewsName("The News name");
        newsEntity.setPublicationTime(LocalDateTime.now());
        List<NewsTextData> newsTextDataList = new ArrayList<>();
        if (textData.getChildrenTextData().size() > 0) {
            for (TextData data : textData.getChildrenTextData()) {
                NewsTextData newsTextData = new NewsTextData();
                newsTextData.setNews(newsEntity);
                newsTextData.setTextData(data.getHtmlString());
                newsTextDataList.add(newsTextData);
            }
        }
//        newsEntity.setNewsTextData(newsTextDataList);
        newsEntity.getNewsTextData().addAll(newsTextDataList);
        log.info("NewsEntity: {}", newsEntity);
        log.info("Model loaded");
    }

    @Test
    public void testService_TheVergeOnePageParseComplexPage() {
        assertNotNull(this.theVergeNewsParser);
        ReflectionTestUtils.setField(this.theVergeNewsParser, "parserName", "The Verge");
        ReflectionTestUtils.setField(this.theVergeNewsParser, "resourceUrl", "");
        NewsModel model = this.theVergeNewsParser.processPage(complexPage);
        log.info("Model loaded");
    }

    @Test
    public void jsoupTheVergeLoaderTest() {
        String resource = "https://www.theverge.com";
        try {
            Document doc = Jsoup.connect(resource).get();
            Elements elements = doc
                    .select("div.duet--content-cards--content-card.group");
            List<String> urls = new ArrayList<>();
            for (Element element : elements) {
                String dateStr = null;
                try {
                    dateStr = element.childNodes()
                            .get(0).childNodes()
                            .get(1).childNodes()
                            .get(2).childNodes()
                            .get(1).childNodes()
                            .get(0).attr("datetime");
                } catch (IndexOutOfBoundsException ie) {
                    log.error(ie.getMessage());
                }
                if (dateStr == null || dateStr.isBlank()) {
                    continue;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
                if (LocalDateTime.now().getDayOfMonth() != date.getDayOfMonth()) {
                    log.warn("Processed date is not \"today\"");
                    break;
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
            for (String url : urls) {
                log.info("Page url: {}{}", resource, url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void jsoupTheVergeNewsPageLoader() {
        String pageUrl = "https://www.theverge.com/24087874/smart-kitchen-appliances-cooking-cleaning-vergecast";
        try {
            Document doc = Jsoup.connect(pageUrl).get();
            Elements article = doc.select("article#content.mx-auto.my-24.w-full.max-w-container-lg");
            String pageHeader = article.select("h1.mb-28.font-polysans.text-45.font-bold.leading-100").text();
            String newsDescription = article.select("span.font-polysans.text-22.font-light.leading-110").select("h2").text();
            Elements textData = article.select("div.duet--article--article-body-component-container.clearfix");
            String publishedTime = article.select("div.duet--article--date-and-comments.mb-20.inline-block.font-polysans.text-12.text-gray-5a")
                    .get(0).children()
                    .attr("datetime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            LocalDateTime date = LocalDateTime.parse(publishedTime, formatter);
            Element textDataElements = (Element) article
                    .get(0).childNodes() // get div which contains all text data
                    .get(1).childNodes() // get inner div with data
                    .get(0).childNodes() //
                    .get(0); // main div with data
            List<String> listOfText = new LinkedList<>();
            for (Node element : textDataElements.childNodes()) {
                if (element.attr("class").startsWith("duet--article--article-body-component clear-both block")) {
                    log.info("Skipping duet--article--article-body-component clear-both block");
                    continue;
                }
                Element elementText;
                try {
                    elementText = (Element) element.childNodes().get(0);
                } catch (IndexOutOfBoundsException ie) {
                    log.error("IndexOutOfBound exception for text search");
                    continue;
                }
                StringBuilder collectedText = new StringBuilder();
                for (Node textNodes : elementText.childNodes()) {
                    if (textNodes instanceof TextNode) {
                        collectedText.append(" ");
                        collectedText.append(((TextNode) textNodes).text().trim());
                    }
                }
                listOfText.add(collectedText.toString());
            }
            log.info(pageHeader);
            log.info(newsDescription);
            log.info("Parsed text: ");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void processPageTest() {
        try {
            Document doc = Jsoup.connect(pageWithLi).get();
            Elements article = doc.select("article#content.mx-auto.my-24.w-full.max-w-container-lg");
            String pageHeader = article.select("h1.mb-28.font-polysans.text-45.font-bold.leading-100").text();
            String newsDescription = article.select("span.font-polysans.text-22.font-light.leading-110").select("h2").text();
            Element articleComponentContainer = article.select("div.duet--article--article-body-component-container.clearfix").first();
            Objects.requireNonNull(articleComponentContainer, "Article component container not found or null!");
            Element articleComponents = articleComponentContainer.child(0);

            log.info("Text");
        } catch (IOException e) {
            log.error("First try-catch: {}", e.getMessage());
        }
    }
}
