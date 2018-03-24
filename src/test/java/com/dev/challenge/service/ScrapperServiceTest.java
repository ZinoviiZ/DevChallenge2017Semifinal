package com.dev.challenge.service;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.worker.AllPagesScrapper;
import com.dev.challenge.worker.PagesScrapper;
import com.dev.challenge.worker.PagesScrapperTest;
import com.dev.challenge.worker.ThreadExecutor;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScrapperServiceTest {

    @Autowired private PageRepository pageRepository;
    @Autowired private ScrapingService scrapingService;

    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @Test
    public void savePagesTest() throws IOException, ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<Page> pages = executor.submit(new PagesScrapper(Arrays.asList("http://brovary-rada.gov.ua/documents/27111.html", "http://brovary-rada.gov.ua/documents/27252.html"))).get();
        pages.get(1).setId(pages.get(0).getId());
        scrapingService.savePages(pages);

        Page newPage = pageRepository.findOne("27111");
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        List<Patch> patches = diffMatchPatch.patchFromText(newPage.getDiffs().get(0).getDiff());
        String text2 = (String) diffMatchPatch.patchApply((LinkedList<Patch>) patches, newPage.getOriginalText())[0];

        Assert.assertEquals(pages.get(1).getOriginalText(), text2);
        Assert.assertNotEquals(pages.get(0).getOriginalText(), text2);
        pageRepository.delete("27111");
    }
}
