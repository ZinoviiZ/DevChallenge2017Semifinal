package com.dev.challenge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Error codes with and their error messages.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(1, "Internal server error"),

    PAGE_NOT_FOUND(10, "Page/pages not found"),
    INCORRECT_PAGES_PAGINATION(11, "Incorrect pagination index or pack"),
    PAGE_NOT_HAVE_CURRENT_VERSIONS(12, "Page doesn't have current version"),
    INCORRECT_PAGE_STATUS(13, "Incorrect page status in request. Allowed : ORIGINAL/CHANGED/DELETED");

    private Integer errorCode;
    private String errorMessage;
}
