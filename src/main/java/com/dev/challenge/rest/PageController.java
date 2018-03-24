package com.dev.challenge.rest;

import com.dev.challenge.error.PageException;
import com.dev.challenge.model.response.MessageResponse;
import com.dev.challenge.model.response.PageResponse;
import com.dev.challenge.model.response.PagesResponse;
import com.dev.challenge.service.PageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Entry point for page requests.
 */

@CrossOrigin
@RestController
@RequestMapping(value = "/rest/pages")
@Api(value = "API for working with pages.",
        description = "This API provides the capability to get information about documents and they versions", produces = "application/json")
public class PageController {

    @Autowired private PageService pageService;

    /**
     * Get links on documents by status. Status is not required, so in that way return list of links on differences documents.
     * @param index
     * @param pack
     * @param status
     * @return PagesResponse
     * @throws PageException
     */
    @ApiOperation(value = "Get links on documents by status. Status is not required, so in that way return list of links on differences documents.", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "index", value = "Index of packages of links. Starts with 0",
                    dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "pack", value = "Package size",
                    dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "status", value = "Document's status. Allowed : ORIGINAL,CHANGED,DELETED.",
                    dataType = "String", paramType = "query")})
    @RequestMapping(method = GET)
    public MessageResponse<PagesResponse> getPages(@RequestParam(value = "index") Integer index,
                                                   @RequestParam(value = "pack") Integer pack,
                                                   @RequestParam(value = "status", required = false) String status) throws PageException {
        return pageService.getPages(index, pack, status);
    }

    /**
     * Get information about specific document.
     * @param pageId
     * @param version
     * @return
     * @throws PageException
     */
    @ApiOperation(value = "Get information about specific document.", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Page's id", required = true,
                    dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "v", value = "Version of document. If version is null, return last version.",
                    dataType = "int", paramType = "query")})
    @RequestMapping(value = "/{id}", method = GET)
    public MessageResponse<PageResponse> getPage(@PathVariable("id") String pageId, @RequestParam(value = "v", required = false) Integer version) throws PageException {
        return pageService.getPage(pageId, version);
    }

    /**
     * Get the difference of different versions of the document.
     * @param pageId
     * @param version1
     * @param version2
     * @return html of difference
     * @throws PageException
     */
    @ApiOperation(value = "Get the difference of different versions of the document.", produces = "text/html")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Page's id.",
                    dataType = "String", paramType = "path", required = true),
            @ApiImplicitParam(name = "v1", value = "First document's version which will be compare.",
                    dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "v2", value = "Second document's version which will be compare.",
                    dataType = "int", paramType = "query", required = true)})
    @RequestMapping(value = "/{id}/diff", method = GET)
    public ResponseEntity<String> getDiffHtml(@PathVariable("id") String pageId,
                                             @RequestParam("v1") Integer version1,
                                             @RequestParam("v2") Integer version2) throws PageException {
        return pageService.getHtmlDiff(pageId, version1, version2);
    }
}
