package com.dev.challenge.builder;

import com.dev.challenge.error.PageException;
import com.dev.challenge.worker.AllPagesScrapper;
import com.dev.challenge.worker.ThreadExecutor;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerLinkBuilderTest {

    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @Autowired private ControllerLinkBuilder controllerLinkBuilder;
    @LocalServerPort int port;

    @Before
    public void initializer() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setServerPort(port);
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @After
    public void finalizer() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void buildPageLinkTest() throws PageException {

        String pageId = "pageId";
        Integer version = 3;
        String link = controllerLinkBuilder.buildPageLink(pageId, version);
        Assert.assertEquals("http://localhost:" + port + "/rest/pages/" + pageId + "?v=" + version, link);
    }

    @Test
    public void buildHtmlDiffLink() throws PageException {

        String pageId = "pageId";
        Integer version = 3;
        String link = controllerLinkBuilder.buildHtmlDiffLink(pageId, version);
        Assert.assertEquals("http://localhost:" + port + "/rest/pages/" + pageId + "/diff?v1=" + (version - 1) +"&v2=" + version, link);
    }

}