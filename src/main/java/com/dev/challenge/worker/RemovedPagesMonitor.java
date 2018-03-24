package com.dev.challenge.worker;

import com.dev.challenge.builder.DocumentLinkConverter;
import com.dev.challenge.model.entity.Page;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.util.DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dev.challenge.model.entity.PageStatus.DELETED;

/**
 * Thread for checking dates of last document scan and if they are before yesterday, set them like DELETED
 */
@Component
public class RemovedPagesMonitor extends Thread {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired private PageRepository pageRepository;

    @Override
    public void run() {
        
        String twoDaysAgo = DateFormat.yyyyMMddhh.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
        List<Page> pages = pageRepository.findByLastScanBeforeAndStatusNot(twoDaysAgo, DELETED);
        List<String> deletedLinks = new ArrayList<>();
        for (Page page : pages) {
            page.setStatus(DELETED);
            pageRepository.save(page);
            deletedLinks.add(DocumentLinkConverter.buildDocumentLink(page.getId()));
        }
        LOGGER.info("RemovedPagesMonitor deleted " + pages.size() + " links : " + String.join(" , " + deletedLinks));
    }
}
