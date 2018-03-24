package com.dev.challenge.service;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.Page.PageDiff;
import com.dev.challenge.repository.PageRepository;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.dev.challenge.builder.DocumentLinkConverter.getDocumentId;
import static com.dev.challenge.model.entity.PageStatus.CHANGED;
import static com.dev.challenge.util.DateFormat.yyyyMMddhhMM;

/**
 * Service for scrapped pages processing
 */
@Service
public class ScrapingService {

    @Autowired private PageRepository pageRepository;

    /**
     * Saving scrapped pages in DB. If specific document already exists, save his new version.
     */
    public void savePages(List<Page> pages) {

        for (Page page : pages) {
            page.setId(page.getId());
            savePage(page);
        }
    }

    private void savePage(Page newPage) {

        Page existedPage = pageRepository.findOne(newPage.getId());
        if (existedPage == null) {
            pageRepository.save(newPage);
            return;
        }

        if (existedPage.getDiffs() == null) existedPage.setDiffs(new ArrayList<>());
        String newContent = newPage.getOriginalText();
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        String existedContent = getLastContent(existedPage, diffMatchPatch);
        if (existedContent.equals(newPage.getOriginalText()))
            return;

        LinkedList<DiffMatchPatch.Patch> patches = diffMatchPatch.patchMake(existedContent, newContent);
        String diff = diffMatchPatch.patchToText(patches);
        existedPage.getDiffs().add(new PageDiff(diff, yyyyMMddhhMM.format(new Date())));
        existedPage.setLastScan(newPage.getLastScan());
        existedPage.setStatus(CHANGED);
        existedPage.setAddDate(newPage.getAddDate());
        existedPage.setVersion(existedPage.getVersion() + 1);
        pageRepository.save(existedPage);
    }

    private String getLastContent(Page page, DiffMatchPatch diffMatchPatch) {

        String content = page.getOriginalText();
        for (PageDiff pageDiff : page.getDiffs()) {
            LinkedList<DiffMatchPatch.Patch> patches = (LinkedList<DiffMatchPatch.Patch>) diffMatchPatch.patchFromText(pageDiff.getDiff());
            content = (String) diffMatchPatch.patchApply(patches, content)[0];
        }
        return content;
    }
}
