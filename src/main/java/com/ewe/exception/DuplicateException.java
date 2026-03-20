package com.ewe.exception;

public class DuplicateException extends Exception {
    private static final long serialVersionUID = 4867645708199153376L;

    public DuplicateException() {
    }

    public DuplicateException(String arg0) {
        super(arg0);
    }

    public DuplicateException(Throwable arg0) {
        super(arg0);
    }

    public DuplicateException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
