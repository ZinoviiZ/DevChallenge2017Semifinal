package com.dev.challenge.worker;

import com.dev.challenge.model.entity.Page;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dev.challenge.builder.DocumentLinkConverter.getDocumentId;
import static com.dev.challenge.util.DateFormat.yyyyMMdd;

/**
 * Thread for document scrapping. Thread is initialing with list of document's links.
 * Scrapping content in tag <div class="row otstupVertVneshn"></div>
 * Thread return list of pages
 */
@AllArgsConstructor
public class PagesScrapper implements Callable<List<Page>> {

    private final SimpleDateFormat parsedDataFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private List<String> documentLinks;

    @Override
    public List<Page> call() {

        List<Page> pages = new ArrayList<>();
        List<String> scrappedLinks = new ArrayList<>();
        for (String link : documentLinks) {
            try {
                Page page = parsePage(link);
                pages.add(page);
                scrappedLinks.add(link);
            } catch (Exception ex) {
                LOGGER.error("page scrapper error", ex);
            }
        }
        LOGGER.info("PageScrapper scrapped [" + scrappedLinks.size() + "/" + documentLinks.size() + "]. Next links: " + String.join(" , ", scrappedLinks));
        return pages;
    }

    private Document getDocument(String url) throws IOException {

        return  Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2840.71 Safari/537.36")
                .timeout(20 * 1000)
                .get();
    }

    private Page parsePage(String link) throws Exception {

        Document document = null;
            document = getDocument(link);
        Elements elements = document.select("body > content > div.container-fluid.bg2-content > div.container > div.row.otstupVertVneshn");
        String mainHtmlContent = elements.html();
        String documentHtmlContent = document.select("div.bg1-content.col-md-8.col-sm-8").html();
        String addDocumentDate = null;
        Matcher matcher = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- .| ](0[1-9]|1[012])[- .| ](19|20)\\d\\d").matcher(documentHtmlContent);
        while (matcher.find()) {
            addDocumentDate = matcher.group();
            addDocumentDate = addDocumentDate.replaceAll(" ", ".").replaceAll("-", ".");
            addDocumentDate = yyyyMMdd.format(parsedDataFormat.parse(addDocumentDate));
            break;
        }
        Page page = new Page(getDocumentId(link), mainHtmlContent, addDocumentDate);
        return page;
    }
}
