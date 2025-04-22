package com.intelizign.career.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
public class CustomExceptions {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public static class ResourceNotFoundException extends RuntimeException {

		public ResourceNotFoundException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	public static class DuplicateResourceException extends RuntimeException {
		public DuplicateResourceException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public static class EmailException extends RuntimeException {
		public EmailException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public static class ValidationException extends RuntimeException {
		public ValidationException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	public static class BusinessLogicException extends RuntimeException {
		public BusinessLogicException(String message) {
			super(message);
		}
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	public static class UserNotFoundException extends RuntimeException {
	    public UserNotFoundException(String message) {
	        super(message);
	    }
	    
	    public UserNotFoundException(String message, Throwable cause) {
	    	super(message, cause);
	    }
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public static class BadRequestException extends RuntimeException {
	    public BadRequestException(String message) {
	        super(message);
	    }
	}
	
	@ResponseStatus(HttpStatus.CONFLICT)
	public static class JobDeletionException extends RuntimeException {
	    public JobDeletionException(String message) {
	        super(message);
	    }
	    
	    public JobDeletionException(String message, Throwable cause) {
	        super(message, cause);
	    }
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public static class UnauthorizedException extends RuntimeException {
	    public UnauthorizedException(String message) {
	        super(message);
	    }
	}
}

