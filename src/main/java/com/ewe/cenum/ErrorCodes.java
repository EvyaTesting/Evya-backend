package com.ewe.cenum;

public enum ErrorCodes {
    
    // User-related errors
    REG_FULLNAME_NULL(1001, "Full Name is required"),
    REG_FIRSTNAME_INVALID(1002, "Full Name is invalid, contains only letters"),
    REG_USERNAME_NULL(1003, "Username is required"),
    REG_EMAIL_NULL(1004, "Email is required"),
    REG_EMAIL_INVALID(1005, "Email is invalid"),
    REG_MOBILE_NULL(1006, "Mobile number is required"),
    REG_MOBILE_INVALID(1007, "Mobile number is invalid"),
    REG_PASSWORD_NULL(1008, "Password is required"),
    REG_PASSWORD_INVALID(1009, "Password must be at least 8 characters"),
    REG_PASSWORD_MISMATCH(1010, "Password and Confirm Password must match"),

    // Address-related errors
    REG_ADDRESS_NULL(1011, "Address is required"),
    REG_CITY_NULL(1012, "City is required"),
    REG_COUNTRY_NULL(1013, "Country is required"),
    REG_STATE_NULL(1014, "State is required"),
    REG_ZIPCODE_NULL(1015, "ZIP code is required"),
    REG_ZIPCODE_INVALID(1016, "ZIP code is invalid"),
    
    // User role and device errors
    ROLE_UPDATE_FAILED(1017, "Failed to update user role"),
    DEVICE_UPDATE_FAILED(1018, "Failed to save device details"),

    // General errors
    INTERNAL_SERVER_ERROR(5000, "Internal server error during user registration"),
    INVALID_USER(1019, "Invalid User"),

    // 🆕 Duplicates
    USERNAME_ALREADY_EXISTS(1020, "Username already exists"),
    EMAIL_ALREADY_EXISTS(1021, "Email already exists"),
    MOBILE_ALREADY_EXISTS(1022, "Mobile number already exists"),
	RFID_NOT_FOUND(2001, "RFID not found"),
	INSUFFICIENT_BALANCE(2002, "Insufficient balance for RFID request"),
	RFID_REQUEST_NOT_FOUND(2003, "RFID request not found"),
	RFID_ALREADY_ACTIVE(2004, "RFID is already active"),
	RFID_ALREADY_INACTIVE(2005, "RFID is already inactive");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return message;
    }
}
