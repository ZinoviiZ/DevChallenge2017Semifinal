package com.dev.challenge.service.helper;

import com.dev.challenge.error.PageException;
import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.PageStatus;
import com.dev.challenge.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dev.challenge.error.ErrorCode.INCORRECT_PAGES_PAGINATION;
import static com.dev.challenge.error.ErrorCode.PAGE_NOT_FOUND;

/**
 * Class for page DB processing
 */
@Component
public class PageDbHelper {

    @Autowired private PageRepository pageRepository;

    /**
     * @param index
     * @param packageSize
     * @param status
     * @return package of Pages
     * @throws PageException
     */
    public List<Page> findPagePackage(Integer index, Integer packageSize, PageStatus status) throws PageException {

        if (index < 0 || packageSize <= 0) throw new PageException(INCORRECT_PAGES_PAGINATION);
        org.springframework.data.domain.Page<Page> page;
        if (status == null)
            page = pageRepository.findAll(new PageRequest(index, packageSize));
        else
            page = pageRepository.findByStatus(new PageRequest(index, packageSize), status);
        return page.getContent();
    }
}
