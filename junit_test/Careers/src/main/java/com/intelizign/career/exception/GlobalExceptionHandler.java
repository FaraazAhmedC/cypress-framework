package com.intelizign.career.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.intelizign.career.exception.CustomExceptions.BadRequestException;
import com.intelizign.career.exception.CustomExceptions.DuplicateResourceException;
import com.intelizign.career.exception.CustomExceptions.EmailException;
import com.intelizign.career.exception.CustomExceptions.ResourceNotFoundException;
import com.intelizign.career.exception.CustomExceptions.UserNotFoundException;
import com.intelizign.career.exception.CustomExceptions.ValidationException;
import com.intelizign.career.response.LoginResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// Handle Validation Errors (DTO validation errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
    
//    //Working
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<LoginResponse> handleBadCredentials(BadCredentialsException ex) {
//        return new ResponseEntity<>(new LoginResponse("Invalid email or password", null), HttpStatus.UNAUTHORIZED);
//    }
//    
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<LoginResponse> handleAuthenticationException(AuthenticationException ex) {
//        return new ResponseEntity<>(new LoginResponse("Authentication failed", null), HttpStatus.UNAUTHORIZED);
//    }
//    
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<LoginResponse> handleBadRequest(BadRequestException ex) {
//        return new ResponseEntity<>(new LoginResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
//    }
    
    @ExceptionHandler(CustomExceptions.UserNotFoundException.class)
    public static ResponseEntity<Object> handleDuplicateResourceException(UserNotFoundException ex) 
	{
    	HttpStatus statuscode = HttpStatus.NOT_FOUND;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", ex.getMessage());
		map.put("status", false);
		map.put("statuscode", statuscode.value());
		map.put("data", null);
		return new ResponseEntity<>(map, statuscode);
	}
    
    @ExceptionHandler(TokenRefreshException.class)
    public static ResponseEntity<Object> handleForbiddenException(AccessDeniedException ex)
   	{
       	HttpStatus statuscode = HttpStatus.FORBIDDEN;
   		Map<String, Object> map = new LinkedHashMap<>();
   		map.put("message", ex.getMessage());
   		map.put("status", false);
   		map.put("statuscode", statuscode.value());
   		map.put("data", null);
   		return new ResponseEntity<>(map, statuscode);
   	}
    
    @ExceptionHandler(CustomExceptions.ResourceNotFoundException.class)
    public static ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) 
	{
    	HttpStatus statuscode = HttpStatus.NOT_FOUND;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", ex.getMessage());
		map.put("status", false);
		map.put("statuscode", statuscode.value());
		map.put("data", null);
		return new ResponseEntity<>(map, statuscode);
	}
    
    @ExceptionHandler(CustomExceptions.DuplicateResourceException.class)
    public static ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException ex) 
	{
    	HttpStatus statuscode = HttpStatus.OK;
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", ex.getMessage());
		map.put("status", false);
		map.put("statuscode", statuscode.value());
		map.put("data", null);
		return new ResponseEntity<>(map, statuscode);
	}
    
    @ExceptionHandler(CustomExceptions.EmailException.class)
    public static ResponseEntity<Object> handleEmailException(EmailException ex) 
   	{
       	HttpStatus statuscode = HttpStatus.SERVICE_UNAVAILABLE;
   		Map<String, Object> map = new LinkedHashMap<>();
   		map.put("message", ex.getMessage());
   		map.put("status", false);
   		map.put("statuscode", statuscode.value());
   		map.put("data", null);
   		return new ResponseEntity<>(map, statuscode);
   	}
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
       
        String message = "Duplicate resource found, violates unique constraint!";    
        // For example, you can check the exception's root cause (the database error message)
        if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
            message = "A resource with this value already exists.";
        }
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("message", message);
        responseBody.put("status", false);
        responseBody.put("statusCode", HttpStatus.CONFLICT.value());
        responseBody.put("data", null);

        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    
    @ExceptionHandler(CustomExceptions.ValidationException.class)
    public static ResponseEntity<Object> handleValidationException(ValidationException ex) 
   	{
       	HttpStatus statuscode = HttpStatus.BAD_REQUEST;
   		Map<String, Object> map = new LinkedHashMap<>();
   		map.put("message", ex.getMessage());
   		map.put("status", false);
   		map.put("statuscode", statuscode.value());
   		map.put("data", null);
   		return new ResponseEntity<>(map, statuscode);
   	}
    
 // Handle missing path variable
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing required path variable: " + ex.getVariableName(),
                ex
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle generic bad requests
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid argument provided: " + ex.getMessage(),
                ex
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Handle Business Logic Failures (e.g., Recruiter Not Activated)
    @ExceptionHandler(CustomExceptions.BusinessLogicException.class)
    public ResponseEntity<Object> handleBusinessLogicException(CustomExceptions.BusinessLogicException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    // Handle Any Other Unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // Common method to build error responses
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, status);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", "Invalid API endpoint: " + ex.getRequestURL());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
