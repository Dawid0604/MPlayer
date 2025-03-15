package pl.dawid0604.mplayer.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;

import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<?> handleException(final Exception exception) {
        return new ResponseEntity<>(Map.of("Message", exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<?> handleResourceException(final Exception exception) {
        return new ResponseEntity<>(Map.of("Message", exception.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler({ ResourceExistException.class })
    public ResponseEntity<?> handleResourceExistException(final Exception exception) {
        return new ResponseEntity<>(Map.of("Message", exception.getMessage()), CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(final MethodArgumentNotValidException exception) {
        var body = exception.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .filter(_fieldError -> isNotBlank(_fieldError.getField()) && isNotBlank(_fieldError.getDefaultMessage()))
                            .collect(toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.status(BAD_REQUEST)
                             .body(body);
    }
}
