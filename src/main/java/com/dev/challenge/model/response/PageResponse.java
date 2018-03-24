package com.dev.challenge.model.response;

import com.dev.challenge.model.entity.PageStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response model of page
 */
@Data
@EqualsAndHashCode
public class PageResponse extends ApiResponse {

    private String link;
    private PageStatus status;
    private Integer version;
    private String addDocumentDate;
    private String scanDate;
    private String linkPrettyHtmlDifference;
    private String htmlContent;
}
