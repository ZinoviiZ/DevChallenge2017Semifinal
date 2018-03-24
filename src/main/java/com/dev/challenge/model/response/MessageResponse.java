package com.dev.challenge.model.response;

import com.dev.challenge.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response entity
 */
@Data
S
public class MessageResponse <T extends ApiResponse> {

    private int status;
    private String message;
    private T response;

    public MessageResponse(ErrorCode errorCode) {

        this.status = errorCode.getErrorCode();
        this.message = errorCode.getErrorMessage();
    }

    @JsonCreator
    public MessageResponse(T response) {
        this.response = response;
    }
}
