package com.onepiece.exception;

public class ReviewException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReviewException(String message) {
		super(message);
	}

	public ReviewException(String message, Throwable cause) {
		super(message, cause);
	}

}
