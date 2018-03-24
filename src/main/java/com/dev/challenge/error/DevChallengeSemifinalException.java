package com.dev.challenge.error;

import lombok.Data;

/**
 * Common exception
 */
@Data
public abstract class DevChallengeSemifinalException extends Exception {

    protected ErrorCode errorCode;

    public DevChallengeSemifinalException(ErrorCode errorCode) {
        
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
