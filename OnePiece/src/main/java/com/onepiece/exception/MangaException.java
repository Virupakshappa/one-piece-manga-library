package com.onepiece.exception;

public class MangaException extends Exception {

	private static final long serialVersionUID = 1L;

	public MangaException(String message) {
		super(message);
	}

	public MangaException(String message, Throwable cause) {
		super(message, cause);
	}

}
