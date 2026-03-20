package com.ewe.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorCtrl {

static final Logger logger = LoggerFactory.getLogger(ErrorCtrl.class);

@ExceptionHandler(ServerException.class)
@ResponseStatus(HttpStatus.BAD_GATEWAY)
@ResponseBody
public ServerException serverError(ServerException ex) {
logger.error("Internal server error: {}, cause: {}", ex.getMessage(), ex.getCause());
return ex;
}

@ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@ResponseBody
public ServerException usernameError(Exception ex) {
logger.error("Internal server error: {}, cause: {}", ex.getMessage(), ex.getCause());

return new ServerException(com.ewe.messages.Error.AUTHENTICATION.toString(),
Integer.toString(com.ewe.messages.Error.AUTHENTICATION.getCode()), ex);
}

@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@ResponseBody
public String unknownError(Exception e) {
String error = "Ops! Unknown error occurred.";
String fullCauseMessage = "";

if (e != null) {
if (StringUtils.hasText(e.getMessage())) {
error = e.getMessage();
}
Throwable rootCause = getRootCause(e);
//here i added this part && !rootCause.getMessage().equals(error)) to show only error message but not the cause
if (rootCause != null && StringUtils.hasText(rootCause.getMessage()) && !rootCause.getMessage().equals(error)) {
fullCauseMessage = rootCause.getMessage();
}
}

logger.error("Unknown error: {}, Root cause: {}", error, fullCauseMessage, e);
return error + (StringUtils.hasText(fullCauseMessage) ? " | Cause: " + fullCauseMessage : "");
}
private Throwable getRootCause(Throwable ex) {
   Throwable cause = ex;
   while (cause.getCause() != null && cause != cause.getCause()) {
       cause = cause.getCause();
   }
   return cause;
}

}