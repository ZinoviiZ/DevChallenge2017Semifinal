package com.dev.challenge.worker;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.service.ScrapingService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.dev.challenge.builder.DocumentLinkConverter.PAGINATOR_URL;
import static com.dev.challenge.builder.DocumentLinkConverter.ROOT_URL;


/**
 * Thread goes by pages like http://brovary-rada.gov.ua/documents/?start=i i=0, 10, 20 ..., scrap links on documents,
 * and start new PagesScrapper thread by every 10 links, until.
 * Every 1000 Documents saving in DB
 */
@Component
public class AllPagesScrapper extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Integer THREAD_COUNT = 20;

    @Value("${random}")
    public Boolean isRandom;

    @Value("${pages}")
    public Integer size;

    @Autowired private ScrapingService scrapingService;

    @Override
    public void run() {

        LOGGER.info("AllPagesScrapper starts with parameters: random=" + isRandom + " and pages = " + size);

        int pagesSize = size <= 0 ? Integer.MAX_VALUE : size;
        boolean isEnd = false;
        List<String> allPageIds = new ArrayList<>();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            List<Future<List<Page>>> futures = new ArrayList<>();
            for (int i = 0; !isEnd; i += 1000) {
                for (int j = i; j < i + 1000; j += 10) {

                    List<String> pageLinks = getPageLinks(PAGINATOR_URL + j);
                    if (pageLinks.isEmpty()) {
                        isEnd = true;
                        break;
                    }
                    pagesSize = recountPagesSize(pageLinks, pagesSize);
                    futures.add(executor.submit(new PagesScrapper(pageLinks)));
                    if (pagesSize == 0) {
                        isEnd = true;
                        break;
                    }
                }
                savePagesInBd(futures, allPageIds);
                futures.clear();
            }
        } catch (Exception ex) {
            LOGGER.error("AllPagesScrapper error", ex);
        }
        LOGGER.info("AllPagesScrapper finish");
    }

    private List<String> getPageLinks(String url) throws IOException {

        Document documentList = getDocument(url);
        Elements contents = documentList.select("body > content > div.container-fluid.bg2-content > div.container > div.row.otstupVertVneshn > div.bg1-content.col-md-8.col-sm-8");
        Elements hrefs = contents.select("a[href]");
        List<String> links = new ArrayList<>();
        for (Element href : hrefs) {
            String link = href.attr("href");
            if (link.contains("/documents") && !link.contains("?start="))
                links.add(ROOT_URL + link);
        }
        return links;
    }

    private Document getDocument(String url) throws IOException {

        return  Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2840.71 Safari/537.36")
                .timeout(20 * 1000)
                .get();
    }

    private Integer recountPagesSize(List<String> links, Integer pagesSize) {

        if (pagesSize > links.size()) {
            pagesSize -= links.size();
        } else if(pagesSize == links.size()) {
            pagesSize = 0;
        } else {
            links = links.subList(0, pagesSize);
            pagesSize = 0;
        }
        return pagesSize;
    }

    private void savePagesInBd(List<Future<List<Page>>> futures, List<String> allPageIds) throws ExecutionException, InterruptedException {

        List<Page> pages = new ArrayList<>();
        for (Future<List<Page>> future : futures) {
            pages.addAll(future.get());
        }

        for (Page page : pages)
            allPageIds.add(page.getId());

        if (isRandom) makeChangedContent(pages, allPageIds);

        scrapingService.savePages(pages);
    }

    public void makeChangedContent(List<Page> pages, List<String> pageIds) {

        Random random = new Random();
        int i = 0;
        for (Page page : pages) {
            i++;
            if (i % 10 == 0) {
                String id = pageIds.get(random.nextInt(i));
                page.setId(id);
            }
        }
    }
}