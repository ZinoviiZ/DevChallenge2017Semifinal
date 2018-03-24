package com.dev.challenge.worker;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.builder.DocumentLinkConverter;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.service.ScrapingService;
import com.dev.challenge.util.DateFormat;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Thread for monitoring documents depends on their document added date.
 * For example fromDays=0, toDays=7 - thread is monitoring documents which addDate in diapason [today - 7, today]
 *
 */
@AllArgsConstructor
public class PagesMonitor extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Integer THREAD_COUNT = 5;

    private Integer fromDays;
    private Integer toDays;
    private PageRepository pageRepository;
    private ScrapingService scrapingService;

    @Override
    public void run() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - toDays);
        String fromDate = DateFormat.yyyyMMdd.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + toDays - fromDays);
        String toDate = DateFormat.yyyyMMdd.format(calendar.getTime());
        LOGGER.info("Monitoring documents for day interval [" + fromDate + " - " + toDate + "] starts");
        try {
            List<Page> pages = pageRepository.findByAddDateBetween(fromDate, toDate);
            if (pages.isEmpty()) return;
            List<String> pageLinks = new ArrayList<>();
            for (Page page : pages)
                pageLinks.add(DocumentLinkConverter.buildDocumentLink(page.getId()));
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            LOGGER.info("Monitoring documents for day interval [" + fromDate + " - " + toDate + "] scans next links: " + String.join(" , ", pageLinks));
            Future<List<Page>> future = executor.submit(new PagesScrapper(pageLinks));
            pages = future.get();
            if (pages.isEmpty()) return;
            scrapingService.savePages(pages);
        } catch (Exception ex) {
            LOGGER.error("PageMonitor error", ex);
        }
        LOGGER.info("PageMonitor finish");
    }
}
