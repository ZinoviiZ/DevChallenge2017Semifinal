package com.dev.challenge.worker;

import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.service.ScrapingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Initializing thread by Scheduled
 */
@Component
public class ThreadExecutor {

    @Autowired private AllPagesScrapper allPagesScrapper;
    @Autowired private RemovedPagesMonitor removedPagesMonitor;
    @Autowired private PageRepository pageRepository;
    @Autowired private ScrapingService scrapingService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private ScheduledExecutorService executor;

    @PostConstruct
    private void init() {

        LOGGER.info("Start ThreadExecutor initializing");
        executor = Executors.newScheduledThreadPool(5);
        executor.scheduleAtFixedRate(allPagesScrapper, 10,  24 * 60 * 60 * 1000, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new PagesMonitor(0, 7, pageRepository, scrapingService), 20,  60, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new PagesMonitor(8,30, pageRepository, scrapingService), 40, 120, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new PagesMonitor(31,100, pageRepository, scrapingService), 60, 240, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(new PagesMonitor(101,40000, pageRepository, scrapingService), 80, 480, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(removedPagesMonitor, 120, 300, TimeUnit.SECONDS);
    }
}