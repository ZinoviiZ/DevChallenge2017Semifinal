package com.dev.challenge.aop;

import com.dev.challenge.error.PageException;
import com.dev.challenge.model.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.dev.challenge.error.ErrorCode.INTERNAL_SERVER_ERROR;

/**
 * AOP Exception handler.
 */
@RestControllerAdvice(basePackages = {"com.dev.challenge.rest"})
public class ControllerExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Handle general server exception
     * @param req
     * @param ex
     * @return MessageResponse with error status
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public MessageResponse handleGeneralException(HttpServletRequest req, Exception ex) {
        LOGGER.error("error: ", ex);
        return new MessageResponse(INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle page exception
     * @param req
     * @param ex
     * @return MessageResponse with error status
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(PageException.class)
    public MessageResponse handlePageException(HttpServletRequest req, PageException ex) {
        LOGGER.error("Internal server error: ", ex);
        return new MessageResponse(ex.getErrorCode());
    }
}
