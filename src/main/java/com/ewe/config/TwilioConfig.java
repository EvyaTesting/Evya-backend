package com.ewe.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;

@Configuration
@Component
@ConfigurationProperties(prefix = "twilio")

public class TwilioConfig {
    private String accountSid;
    private String authToken;
    private String phoneNumber;
    
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }
	public String getAccountSid() {
		return accountSid;
	}
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public TwilioConfig(String accountSid, String authToken, String phoneNumber) {
		super();
		this.accountSid = accountSid;
		this.authToken = authToken;
		this.phoneNumber = phoneNumber;
	}
	public TwilioConfig() {
		
	}
	

}
