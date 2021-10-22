package it.units.crossway.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({GameNotFoundException.class})
    protected ResponseEntity<String> handleGameNotFoundException(GameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({GameException.class})
    protected ResponseEntity<String> handleGameException(GameException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({DuplicatePlayerException.class})
    protected ResponseEntity<String> handleDuplicatePlayerException(DuplicatePlayerException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
