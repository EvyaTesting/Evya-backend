package com.ewe.messages;

public enum Error {

	USER_NOT_EXIST(1,"User doesnot exist"),
	AUTHENTICATION(2, "User name or password is incorrect");
	


	/******* Please add Error Codes from 3 onwards ********/

	private final int code;
	private final String description;

	private Error(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "Error Code-" + code + ": " + description;
		// return "" + description;
	}

}
