package com.dev.challenge.model.response;

import lombok.Data;

import java.util.List;

/**
 * Response model of link of pages.
 */
@Data
public class PagesResponse extends ApiResponse {

    private List<String> links;
}
