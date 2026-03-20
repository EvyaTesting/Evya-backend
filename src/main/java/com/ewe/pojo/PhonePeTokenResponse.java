package com.ewe.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PhonePeTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("encrypted_access_token")
    private String encryptedAccessToken;
    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("issued_at")
    private Long issuedAt;

    @JsonProperty("expires_at")
    private Long expiresAt;

    @JsonProperty("session_expires_at")
    private Long sessionExpiresAt;

    public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getEncryptedAccessToken() {
		return encryptedAccessToken;
	}

	public void setEncryptedAccessToken(String encryptedAccessToken) {
		this.encryptedAccessToken = encryptedAccessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Long getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Long issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Long getSessionExpiresAt() {
		return sessionExpiresAt;
	}

	public void setSessionExpiresAt(Long sessionExpiresAt) {
		this.sessionExpiresAt = sessionExpiresAt;
	}

	
}