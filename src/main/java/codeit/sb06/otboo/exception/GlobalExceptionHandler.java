package codeit.sb06.otboo.exception;

import codeit.sb06.otboo.exception.auth.AuthException;
import codeit.sb06.otboo.exception.clothes.ClothesException;
import codeit.sb06.otboo.exception.feed.FeedException;
import codeit.sb06.otboo.exception.message.MessageException;
import codeit.sb06.otboo.exception.profile.ProfileException;
import codeit.sb06.otboo.exception.storage.StorageException;
import codeit.sb06.otboo.exception.user.UserException;
import codeit.sb06.otboo.exception.weather.WeatherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        log.error("AuthException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(RootException.class)
    public  ResponseEntity<ErrorResponse> handleRootException(RootException ex) {
        log.error("RootException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException ex) {
        log.error("UserException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        log.error("MessageException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(ClothesException.class)
    public ResponseEntity<ErrorResponse> handleClothesException(ClothesException ex) {
        log.error("ClothesException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(FeedException.class)
    public ResponseEntity<ErrorResponse> handleFeedException(FeedException ex) {
        log.error("FeedException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(WeatherException.class)
    public ResponseEntity<ErrorResponse> handleWeatherException(WeatherException ex) {
        log.error("WeatherException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<ErrorResponse> handleProfileException(ProfileException ex) {
        log.error("ProfileException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }

    @ExceptionHandler(RedisSystemException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnectionError(RedisSystemException ex) {
        log.error("Redis connection error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(StorageException ex) {
        log.error("StorageException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(ex.getStatus()));
    }
}
