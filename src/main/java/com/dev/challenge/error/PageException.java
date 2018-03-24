package com.dev.challenge.error;

/**
 * Page specific exception.
 */
public class PageException extends DevChallengeSemifinalException {
    
    public PageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
