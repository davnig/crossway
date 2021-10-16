package it.units.crossway.server.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({GameException.class})
    protected ResponseEntity<Object> handleGameException(GameException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({DuplicatePlayerException.class})
    protected ResponseEntity<Object> handleDuplicatePlayerException(DuplicatePlayerException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}
