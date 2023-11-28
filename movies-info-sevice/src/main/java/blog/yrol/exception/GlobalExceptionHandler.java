package blog.yrol.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

/**
 * A class for handling exceptions globally
 * @ControllerAdvice - will be used to catch any exception thrown by the controllers
 * **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handling WebExchangeBindExceptions - thrown due to bean validation & etc
     * **/
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleRequestBodyException(WebExchangeBindException ex) {
        log.error("Exception caught in handleRequestBodyError: {}", ex.getMessage(), ex);

        // Getting each and every error and join then comma separated
        var error = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(","));

        log.error("Error is: {}", error);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
