package com.dev.challenge.service;

import com.dev.challenge.builder.PageResponseBuilder;
import com.dev.challenge.error.PageException;
import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.PageStatus;
import com.dev.challenge.model.response.MessageResponse;
import com.dev.challenge.model.response.PageResponse;
import com.dev.challenge.model.response.PagesResponse;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.service.helper.PageDbHelper;
import com.dev.challenge.service.helper.PageTextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dev.challenge.error.ErrorCode.INCORRECT_PAGE_STATUS;
import static com.dev.challenge.error.ErrorCode.PAGE_NOT_FOUND;
import static com.dev.challenge.error.ErrorCode.PAGE_NOT_HAVE_CURRENT_VERSIONS;

/**
 * Layer of page business logic
 */
@Service
public class PageService {

    @Autowired private PageRepository pageRepository;

    @Autowired private PageResponseBuilder responseBuilder;
    @Autowired private PageDbHelper pageDbHelper;
    @Autowired private PageTextHelper pageHelper;

    /**
     * @param index
     * @param pack
     * @param status
     * @return PageSResponse
     * @throws PageException
     */
    public MessageResponse<PagesResponse> getPages(Integer index, Integer pack, String status) throws PageException {

        PageStatus pageStatus = null;
        if (status != null && (pageStatus = PageStatus.fromString(status)) == null)
            throw new PageException(INCORRECT_PAGE_STATUS);

        List<Page> pages = pageDbHelper.findPagePackage(index, pack, pageStatus);
        PagesResponse response = responseBuilder.buildPagesResponse(pages);
        return new MessageResponse<>(response);
    }

    /**
     *
     * @param pageId
     * @param version
     * @return PageResponse
     * @throws PageException
     */
    public MessageResponse<PageResponse> getPage(String pageId, Integer version) throws PageException {

        Page page = pageRepository.findOne(pageId);
        if (page == null) throw new PageException(PAGE_NOT_FOUND);
        if (version == null || version < 0) version = page.getVersion();
        if (version != 0 && (page.getDiffs() == null || page.getDiffs().size() < version))
                throw new PageException(PAGE_NOT_HAVE_CURRENT_VERSIONS);
        String pageText = pageHelper.getTextPageByVersion(page, version);
        PageResponse response = responseBuilder.buildPageResponse(page, pageText, version);
        return new MessageResponse<>(response);
    }

    public ResponseEntity<String> getHtmlDiff(String pageId, Integer v1, Integer v2) throws PageException {

        Page page = pageRepository.findOne(pageId);
        if (page == null) throw new PageException(PAGE_NOT_FOUND);
        Integer maxVersion = Math.max(v1,v2);
        if ((page.getDiffs() == null && maxVersion != 0) || page.getDiffs().size() < maxVersion)
            throw new PageException(PAGE_NOT_HAVE_CURRENT_VERSIONS);
        String pageTextV1 = pageHelper.getTextPageByVersion(page, v1);
        String pageTextV2 = pageHelper.getTextPageByVersion(page, v2);
        String htmlDifference = pageHelper.getHtmlDifference(pageTextV1, pageTextV2);
        return new ResponseEntity<>(htmlDifference, HttpStatus.OK);
    }
}