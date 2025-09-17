package freeswitch.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle all exceptions globally
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.error("Exception: "+ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }

    // Handle custom exceptions specifically
    @ExceptionHandler(TbException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(TbException ex) {
        logger.error("Custom Exception: "+ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }
    // Handle Runtime exceptions specifically
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime Exception: "+ ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }
}
