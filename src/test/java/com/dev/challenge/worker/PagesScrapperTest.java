package com.dev.challenge.worker;

import com.dev.challenge.model.entity.Page;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PagesScrapperTest {

    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @Test
    public void pagesScrapperTest() throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Page page = executor.submit(new PagesScrapper(Arrays.asList("http://brovary-rada.gov.ua/documents/27252.html"))).get().get(0);
        Assert.assertEquals(page.getId(), "27252");
        Assert.assertEquals(page.getAddDate(), "2017-02-09");
        Assert.assertTrue(page.getOriginalText().contains("“Про затвердження Програми соціально-економічного та культурного розвитку міста на 2017 рік”"));
    }
}
