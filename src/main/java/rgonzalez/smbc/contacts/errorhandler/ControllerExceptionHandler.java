package rgonzalez.smbc.contacts.errorhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import rgonzalez.smbc.contacts.model.dto.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler using RestControllerAdvice
 * Handles exceptions across all controllers and returns standardized
 * ErrorResponse
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

        /**
         * Handles validation errors from request body validation
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                logger.warn("Validation error: {}", ex.getMessage());

                List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> validationErrors.add(new ErrorResponse.ValidationError(
                                                error.getField(),
                                                error.getDefaultMessage())));

                String path = request.getDescription(false).replace("uri=", "");

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                "Validation failed - see validationErrors for details",
                                path,
                                validationErrors);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles IllegalArgumentException
         */
        @ExceptionHandler(IllegalArgumentException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex,
                        WebRequest request) {

                logger.warn("Illegal argument exception: {}", ex.getMessage());

                String path = request.getDescription(false).replace("uri=", "");

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request",
                                ex.getMessage(),
                                path);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * Handles RuntimeException
         */
        @ExceptionHandler(RuntimeException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex,
                        WebRequest request) {

                logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);

                String path = request.getDescription(false).replace("uri=", "");

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                                path);

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        /**
         * Handles 404 Not Found exceptions
         */
        @ExceptionHandler(NoHandlerFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
                        NoHandlerFoundException ex,
                        WebRequest request) {

                logger.warn("Resource not found: {}", ex.getRequestURL());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                "The requested resource was not found",
                                ex.getRequestURL());

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        /**
         * Generic exception handler for any other exceptions
         */
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex,
                        WebRequest request) {

                logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

                String path = request.getDescription(false).replace("uri=", "");

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal Server Error",
                                "An unexpected error occurred. Please contact support.",
                                path);

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
