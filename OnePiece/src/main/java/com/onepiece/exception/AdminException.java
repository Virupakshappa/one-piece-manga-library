package com.onepiece.exception;

public class AdminException extends Exception {

	private static final long serialVersionUID = 1L;

	public AdminException(String message) {
		super(message);
	}

	public AdminException(String message, Throwable cause) {
		super(message, cause);
	}

}
