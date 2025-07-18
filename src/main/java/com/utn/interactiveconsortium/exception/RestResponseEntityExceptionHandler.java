package com.utn.interactiveconsortium.exception;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.logging.log4j.Level.ERROR;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public class RestResponseEntityExceptionHandler {

    private void logException( Throwable throwable) {
        String msg = getMessage(throwable);
        log.log(ERROR, msg, ofNullable(getRootCause(throwable)).map(rootCause -> throwable).orElse(throwable));
    }

    private String getMessage(Throwable throwable) {
        if (isNull(throwable)) {
            return "Generic error information";
        }
        return getRootCauseMessage(throwable);
    }

    @ExceptionHandler(value = {EntityAlreadyExistsException.class})
    public ResponseEntity<Object> handleConflict(EntityAlreadyExistsException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(value = {BookingNotAvailableException.class})
    public ResponseEntity<Object> handleBookingNotAvailable(BookingNotAvailableException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(value = {BookingLimitExceededException.class})
    public ResponseEntity<Object> handleBookingLimitExceeded(BookingLimitExceededException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(value = {IssueReportStatusException.class})
    public ResponseEntity<Object> handleIssueReportStatusException(IssueReportStatusException ex) {
        logException(ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getMessage());
    }

    @ExceptionHandler(value = { CustomIllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(CustomIllegalArgumentException ex){
        logException(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(value = { CustomGenericException.class})
    public ResponseEntity<Object> handleCustomGenericException(CustomGenericException ex){
        logException(ex);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getMessage());
    }

}
