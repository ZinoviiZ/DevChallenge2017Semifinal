package com.dev.challenge.worker;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.PageStatus;
import com.dev.challenge.repository.PageRepository;
import com.dev.challenge.util.DateFormat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static com.dev.challenge.model.entity.PageStatus.DELETED;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RemovedPagesMonitorTest {

    @Autowired private PageRepository pageRepository;
    @Autowired private RemovedPagesMonitor removedPagesMonitor;
    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @Before
    public void initializer() {
        Page page = new Page("test_id", "test_content", "testAddData");
        page.setLastScan(DateFormat.yyyyMMddhh.format(new Date(new Date().getTime() - 25 * 60 * 60 * 1000)));
        pageRepository.save(page);
    }

    @After
    public void finalizer() {
        pageRepository.delete("test_id");
    }

    @Test
    public void removedPagesMonitor() throws InterruptedException {

        removedPagesMonitor.start();
        Thread.currentThread().sleep(2000);
        Page page = pageRepository.findOne("test_id");
        Assert.assertEquals(page.getStatus(), DELETED);
    }
}
