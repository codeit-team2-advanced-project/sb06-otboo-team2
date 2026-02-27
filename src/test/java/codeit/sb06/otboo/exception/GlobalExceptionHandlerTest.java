package codeit.sb06.otboo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import codeit.sb06.otboo.exception.auth.InvalidTokenException;
import codeit.sb06.otboo.exception.clothes.ClothesException;
import codeit.sb06.otboo.exception.feed.FeedException;
import codeit.sb06.otboo.exception.message.MessageException;
import codeit.sb06.otboo.exception.user.UserException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesAuthException() {
        InvalidTokenException ex = new InvalidTokenException();

        ResponseEntity<ErrorResponse> response = handler.handleAuthException(ex);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("InvalidTokenException", response.getBody().getExceptionName());
    }

    @Test
    void handlesRootException() {
        RootException ex = new RootException("root", 500);

        ResponseEntity<ErrorResponse> response = handler.handleRootException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("root", response.getBody().getMessage());
    }

    @Test
    void handlesUserException() {
        UserException ex = new UserException("user", 404);

        ResponseEntity<ErrorResponse> response = handler.handleUserException(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("UserException", response.getBody().getExceptionName());
    }

    @Test
    void handlesMessageException() {
        MessageException ex = new MessageException("message", 400);

        ResponseEntity<ErrorResponse> response = handler.handleMessageException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("message", response.getBody().getMessage());
    }

    @Test
    void handlesClothesException() {
        ClothesException ex = new ClothesException("clothes", 409);

        ResponseEntity<ErrorResponse> response = handler.handleClothesException(ex);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("ClothesException", response.getBody().getExceptionName());
    }

    @Test
    void handlesFeedException() {
        FeedException ex = new FeedException("feed", 422);

        ResponseEntity<ErrorResponse> response = handler.handleFeedException(ex);

        assertEquals(422, response.getStatusCode().value());
        assertEquals("feed", response.getBody().getMessage());
    }
}
