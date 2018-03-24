package com.dev.challenge.service;

import com.dev.challenge.error.PageException;
import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.response.MessageResponse;
import com.dev.challenge.model.response.PageResponse;
import com.dev.challenge.model.response.PagesResponse;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.worker.AllPagesScrapper;
import com.dev.challenge.worker.PagesScrapper;
import com.dev.challenge.worker.ThreadExecutor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dev.challenge.error.ErrorCode.*;
import static com.dev.challenge.model.entity.PageStatus.CHANGED;
import static com.dev.challenge.model.entity.PageStatus.ORIGINAL;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PageServiceTest {

    @Autowired private PageService pageService;
    @Autowired private PageRepository pageRepository;
    @Autowired private ScrapingService scrapingService;

    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @LocalServerPort
    int port;


    @Before
    public void initializer() throws ExecutionException, InterruptedException, PageException {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setServerPort(port);
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        String text1 = "I'm selfish, impatient and a little insecure. I make mistakes,\n" +
                "I am out of control and at times hard to handle. But if you can't handle me at my worst,\n" +
                "then you sure as hell don't deserve me at my best.";

        String text2 = "I'm selfish, impatient and a little secure. I don't make mistakes,\n" +
                "I am out of control and at times hard to handle difficult things. But if you can't handle me at my worst,\n" +
                "then you sure as hell don't deserve me at my best.";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<Page> pages = executor.submit(new PagesScrapper(Arrays.asList("http://brovary-rada.gov.ua/documents/27252.html", "http://brovary-rada.gov.ua/documents/27107.html", "http://brovary-rada.gov.ua/documents/26521.html"))).get();
        pages.get(1).setOriginalText(text1);
        pages.get(2).setOriginalText(text2);
        pages.get(2).setId(pages.get(1).getId());

        scrapingService.savePages(pages);
    }

    @After
    public void finalizer() {
        RequestContextHolder.resetRequestAttributes();
        pageRepository.delete("27252");
        pageRepository.delete("27107");
        pageRepository.delete("26521");
    }

    @Test
    public void getPagesTest() throws ExecutionException, InterruptedException, PageException {


        MessageResponse<PagesResponse> response = pageService.getPages(2, 3, null);
        Assert.assertTrue(response.getResponse().getLinks().isEmpty());
        response = pageService.getPages(0, 3, null);
        Assert.assertEquals(response.getResponse().getLinks().get(0), "http://localhost:" + port + "/rest/pages/27107?v=1");
        Assert.assertEquals(response.getResponse().getLinks().get(1), "http://localhost:" + port + "/rest/pages/27252?v=0");

        try {
            response = pageService.getPages(2, 3, "NOT_CORRECT_STRING");
            Assert.assertTrue(false);
        } catch (PageException ex) {
            Assert.assertEquals(ex.getErrorCode(), INCORRECT_PAGE_STATUS);
        }
    }

    @Test
    public void getPageTest() throws ExecutionException, InterruptedException, PageException {

        MessageResponse<PageResponse> messageResponse = null;

        try {
            messageResponse = pageService.getPage("NOT_EXIST_PAGE_ID", 1);
            Assert.assertTrue(false);
        } catch (PageException ex) {
            Assert.assertEquals(ex.getErrorCode(), PAGE_NOT_FOUND);
        }

        try {
            messageResponse = pageService.getPage("27252", 1);
            Assert.assertTrue(false);
        } catch (PageException ex) {
            Assert.assertEquals(ex.getErrorCode(), PAGE_NOT_HAVE_CURRENT_VERSIONS);
        }

        messageResponse = pageService.getPage("27252", null);
        PageResponse response = messageResponse.getResponse();
        Assert.assertEquals(response.getLink(), "http://brovary-rada.gov.ua/documents/27252.html");
        Assert.assertTrue(response.getLinkPrettyHtmlDifference() == null);
        Assert.assertEquals(response.getStatus(), ORIGINAL);
        Assert.assertTrue(response.getVersion() == 0);

        messageResponse = pageService.getPage("27107", 1);
        response = messageResponse.getResponse();
        Assert.assertEquals(response.getLink(), "http://brovary-rada.gov.ua/documents/27107.html");
        Assert.assertEquals(response.getLinkPrettyHtmlDifference(), "http://localhost:" + port + "/rest/pages/27107/diff?v1=0&v2=1");
        Assert.assertEquals(response.getStatus(), CHANGED);
        Assert.assertTrue(response.getVersion() == 1);
    }

    @Test
    public void getHtmlDiffTest() throws ExecutionException, InterruptedException, PageException {

        String htmlDiff = "<span>I'm selfish, impatient and a little </span><del style=\"background:#ffe6e6;\">in</del><span>secure. I</span><ins style=\"background:#e6ffe6;\"> don't</ins><span> make mistakes,&para;<br>I am out of control and at times hard to handle</span><ins style=\"background:#e6ffe6;\"> difficult things</ins><span>. But if you can't handle me at my worst,&para;<br>then you sure as hell don't deserve me at my best.</span>";

        ResponseEntity<String> responseEntity =  null;
        try {
            responseEntity = pageService.getHtmlDiff("NOT_EXIST_PAGE_ID", null, null);
            Assert.assertTrue(false);
        } catch (PageException ex) {
            Assert.assertEquals(ex.getErrorCode(), PAGE_NOT_FOUND);
        }

        try {
            responseEntity = pageService.getHtmlDiff("27107", 1, 100500);
            Assert.assertTrue(false);
        } catch (PageException ex) {
            Assert.assertEquals(ex.getErrorCode(), PAGE_NOT_HAVE_CURRENT_VERSIONS);
        }

        responseEntity = pageService.getHtmlDiff("27107", 0,1);
        Assert.assertEquals(responseEntity.getBody(), htmlDiff);
    }
}
