package com.dev.challenge.model.entity;

/**
 * Document statuses
 */
public enum PageStatus {

    ORIGINAL,
    CHANGED,
    DELETED;

    /**
     * Find PageStatus by String ignoring case.
     * @param key
     * @return PageStatus
     */
    public static PageStatus fromString(String key) {
        for(PageStatus status : PageStatus.values()) {
            if(status.name().equalsIgnoreCase(key)) {
                return status;
            }
        }
        return null;
    }
}
