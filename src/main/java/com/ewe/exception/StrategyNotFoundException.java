package com.ewe.exception;

public class StrategyNotFoundException extends Exception {
   private static final long serialVersionUID = -254961453366709249L;

    public StrategyNotFoundException() {
    }

    public StrategyNotFoundException(String message) {
        super(message);
    }

    public StrategyNotFoundException(Throwable cause) {
        super(cause);
    }

    public StrategyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
