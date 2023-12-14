package com.onepiece.exception;

public class StudentException extends Exception {

	private static final long serialVersionUID = 1L;

	public StudentException(String message) {
		super(message);
	}

	public StudentException(String message, Throwable cause) {
		super(message, cause);
	}

}
