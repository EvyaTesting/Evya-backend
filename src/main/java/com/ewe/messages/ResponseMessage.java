package com.ewe.messages;

import com.ewe.pojo.Referral;

public class ResponseMessage {
	private String message;

	public ResponseMessage(String string) {
		this.message = string;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
