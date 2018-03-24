package com.dev.challenge.builder;

import com.dev.challenge.error.PageException;
import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.response.PageResponse;
import com.dev.challenge.model.response.PagesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for build page's responses
 */
@Component
public class PageResponseBuilder {

    @Autowired private ControllerLinkBuilder controllerLinkBuilder;

    /**
     * Build PageResponse by page, versionText and version
     * @param page
     * @param versionText
     * @param version
     * @return PageResponse
     * @throws PageException
     */
    public PageResponse buildPageResponse(Page page, String versionText, Integer version) throws PageException {

        PageResponse response = new PageResponse();
        response.setHtmlContent(versionText);
        response.setStatus(page.getStatus());
        response.setLink(DocumentLinkConverter.buildDocumentLink(page.getId()));
        if (version > 0)
            response.setLinkPrettyHtmlDifference(controllerLinkBuilder.buildHtmlDiffLink(page.getId(), version));
        response.setVersion(version);
        response.setAddDocumentDate(page.getAddDate());
        response.setScanDate(getVersionScanDate(page, version));
        return response;
    }

    /**
     * Build PagesResponse by list of pages
     * @param pages
     * @return PagesResponse
     * @throws PageException
     */
    public PagesResponse buildPagesResponse(List<Page> pages) throws PageException {

        PagesResponse response = new PagesResponse();
        response.setLinks(new ArrayList<>());
        for (Page page : pages) {
            String link = controllerLinkBuilder.buildPageLink(page.getId(), page.getVersion());
            response.getLinks().add(link);
        }
        return response;
    }

    private String getVersionScanDate(Page page, Integer version) {

        if (version == 0) return page.getLastScan();
        return page.getDiffs().get(version - 1).getScanDate();
    }
}
