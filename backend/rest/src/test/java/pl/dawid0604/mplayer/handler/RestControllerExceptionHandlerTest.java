package pl.dawid0604.mplayer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.dawid0604.mplayer.exception.ResourceExistException;
import pl.dawid0604.mplayer.exception.ResourceNotFoundException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = HandlerSpringBootTestApplicationContext.class)
class RestControllerExceptionHandlerTest {

    @Autowired
    private RestControllerExceptionHandler restControllerExceptionHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandleException() {
        // Given
        String exceptionMessage = "test";

        // When
        var result = restControllerExceptionHandler.handleException(new RuntimeException(exceptionMessage));
        var mappedBody = objectMapper.convertValue(result.getBody(), Map.class);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(mappedBody.containsKey("Message"));
        assertEquals(exceptionMessage, mappedBody.get("Message"));
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        // Given
        Exception exception = ResourceNotFoundException.userException();

        // When
        var result = restControllerExceptionHandler.handleResourceException(exception);
        var mappedBody = objectMapper.convertValue(result.getBody(), Map.class);

        // Then
        assertEquals(NOT_FOUND, result.getStatusCode());
        assertTrue(mappedBody.containsKey("Message"));
        assertEquals(exception.getMessage(), mappedBody.get("Message"));
    }

    @Test
    void shouldHandleResourceExistException() {
        // Given
        Exception exception = ResourceExistException.userNicknameException("xyz");

        // When
        var result = restControllerExceptionHandler.handleResourceExistException(exception);
        var mappedBody = objectMapper.convertValue(result.getBody(), Map.class);

        // Then
        assertEquals(CONFLICT, result.getStatusCode());
        assertTrue(mappedBody.containsKey("Message"));
        assertEquals(exception.getMessage(), mappedBody.get("Message"));
    }

    @Test
    void shouldHandleValidationException() {
        // Given
        String fieldName = "Title";
        String exceptionMessage = "Field is not valid";

        FieldError fieldError = new FieldError("objectName", fieldName, exceptionMessage);
        BindException bindException = new BindException(new Object(), "objectName");
                      bindException.addError(fieldError);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindException);

        // When
        var result = restControllerExceptionHandler.handleValidationException(exception);
        var body = (Map<?, ?>) result.getBody();

        // Then
        assertEquals(BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(body != null && body.containsKey(fieldName));
        assertEquals(exceptionMessage, body.get(fieldName));
    }
}