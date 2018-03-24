package com.dev.challenge.builder;

import com.dev.challenge.error.PageException;
import com.dev.challenge.rest.PageController;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Link builder for HATEOAS methodology.
 */
@Component
public class ControllerLinkBuilder {

    /**
     * Build link on Page by id and version
     * @param id
     * @param version
     * @return Link on PageResponse
     * @throws PageException
     */
    public String buildPageLink(String id, Integer version) throws PageException {

        Link link = linkTo(methodOn(PageController.class).getPage(id, version)).withSelfRel();
        String href = link.getHref();
        return href;
    }

    /**
     * Build html difference by id and version. v1 = version - 1; v2 = version.
     * @param id
     * @param version
     * @return Link on pretty html difference
     * @throws PageException
     */
    public String buildHtmlDiffLink(String id, Integer version) throws PageException {

        Link link = linkTo(methodOn(PageController.class).getDiffHtml(id, version - 1, version)).withSelfRel();
        String href = link.getHref();
        return href;
    }
}
