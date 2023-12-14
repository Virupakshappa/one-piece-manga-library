package com.onepiece.exception;

public class NotificationException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotificationException(String message) {
		super(message);
	}

	public NotificationException(String message, Throwable cause) {
		super(message, cause);
	}

}
