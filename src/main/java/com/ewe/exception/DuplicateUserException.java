package com.ewe.exception;

public class DuplicateUserException extends Exception {
    private static final long serialVersionUID = 6532938055712085137L;

    public DuplicateUserException() {
    }

    public DuplicateUserException(String arg0) {
        super(arg0);
    }

    public DuplicateUserException(Throwable arg0) {
        super(arg0);
    }

    public DuplicateUserException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
