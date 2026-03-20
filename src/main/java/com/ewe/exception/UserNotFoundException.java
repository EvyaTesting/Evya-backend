package com.ewe.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "message", "key", "cause" })
public class UserNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, String key) {
		super(message);
		this.key = key;
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(String message, String key, Throwable cause) {
		super(message, cause);
		this.key = key;
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
