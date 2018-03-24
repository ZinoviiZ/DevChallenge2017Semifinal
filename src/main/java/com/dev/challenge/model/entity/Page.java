package com.dev.challenge.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import static com.dev.challenge.model.entity.PageStatus.ORIGINAL;
import static com.dev.challenge.util.DateFormat.yyyyMMddhhMM;

/**
 * Page entity.
 */
@Data
@Document(collection = "documents")
@NoArgsConstructor
public class Page {

    @Id
    private String id;

    private String addDate;
    private String originalText;
    private List<PageDiff> diffs;
    private String lastScan;
    private PageStatus status = ORIGINAL;
    private Integer version;

    public Page(String link, String originalText, String addDate) {

        this.id = link;
        this.originalText = originalText;
        this.lastScan = yyyyMMddhhMM.format(new Date());
        this.addDate = addDate;
        this.status = ORIGINAL;
        this.version = 0;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageDiff {

        private String diff;
        private String scanDate;
    }
}
