package com.dev.challenge.service.helper;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.worker.AllPagesScrapper;
import com.dev.challenge.worker.ThreadExecutor;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PageTextHelperTest {

    @Autowired private PageTextHelper pageTextHelper;
    @MockBean private ThreadExecutor threadExecutor;
    @MockBean private AllPagesScrapper allPagesScrapper;

    @Test
    public void getTextPageByVersionTest() {

        String text1 = "I'm selfish, impatient and a little insecure. I make mistakes,\n" +
                "I am out of control and at times hard to handle. But if you can't handle me at my worst,\n" +
                "then you sure as hell don't deserve me at my best.";

        String text2 = "I'm selfish, impatient and a little secure. I don't make mistakes,\n" +
                "I am out of control and at times hard to handle difficult things. But if you can't handle me at my worst,\n" +
                "then you sure as hell don't deserve me at my best.";

        String text3 = "I'm selfish, impatient and a little secure. This is third text so I try to don't make mistakes,\n" +
                "I am out of control and at times hard to handle difficult things. But if you can't handle me at my worst,\n" +
                "then you sure don't deserve me at my best.";

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        Page page = new Page();
        page.setDiffs(new ArrayList<>());
        page.setOriginalText(text1);
        LinkedList<Patch> patches1 = diffMatchPatch.patchMake(text1, text2);
        page.getDiffs().add(new Page.PageDiff(diffMatchPatch.patchToText(patches1), "date1"));
        LinkedList<Patch> patches2 = diffMatchPatch.patchMake(text2, text3);
        page.getDiffs().add(new Page.PageDiff(diffMatchPatch.patchToText(patches2), "date2"));

        Assert.assertEquals(text1, pageTextHelper.getTextPageByVersion(page, 0));
        Assert.assertEquals(text2, pageTextHelper.getTextPageByVersion(page, 1));
        Assert.assertEquals(text3, pageTextHelper.getTextPageByVersion(page, 2));
    }
}
