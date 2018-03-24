package com.dev.challenge.service.helper;

import com.dev.challenge.model.entity.Page;
import com.dev.challenge.model.entity.Page.PageDiff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Patch;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for page text process;
 */
@Component
public class PageTextHelper {


    /**
     * @param page
     * @param version
     * @return page version text
     */
    public String getTextPageByVersion(Page page, Integer version) {

        String originalText = page.getOriginalText();
        if (version == 0) return originalText;
        List<PageDiff> pageDiffs = page.getDiffs().subList(0, version);
        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        for (PageDiff pageDiff : pageDiffs) {

            String patchText = pageDiff.getDiff();
            LinkedList<Patch> patches = (LinkedList<Patch>) diffMatchPatch.patchFromText(patchText);
            originalText = (String) diffMatchPatch.patchApply(patches, originalText)[0];
        }
        return originalText;
    }


    /**
     * Get pretty html difference of two texts
     */
    public String getHtmlDifference(String text1, String text2) {

        DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
        List<Diff> diffs = diffMatchPatch.diffMain(text1, text2);
        return diffMatchPatch.diffPrettyHtml((LinkedList<Diff>) diffs);
    }
}
