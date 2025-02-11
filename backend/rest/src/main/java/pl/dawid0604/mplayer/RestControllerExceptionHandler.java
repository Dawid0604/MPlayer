package pl.dawid0604.mplayer;

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
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<?> handleException(final Exception exception) {
        return new ResponseEntity<>(Map.of("Message", exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ResourceNotFoundException.class, ResourceExistException.class })
    public ResponseEntity<?> handleResourceNotFoundException(final Exception exception) {
        return new ResponseEntity<>(Map.of("Message", exception.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(final MethodArgumentNotValidException exception) {
        var body = exception.getBindingResult()
                            .getFieldErrors()
                            .stream()
                            .filter(_fieldError -> isNotBlank(_fieldError.getField()) && isNotBlank(_fieldError.getDefaultMessage()))
                            .collect(toMap(FieldError::getField, FieldError::getDefaultMessage));

        return ResponseEntity.status(BAD_REQUEST)
                             .body(body);
    }
}
