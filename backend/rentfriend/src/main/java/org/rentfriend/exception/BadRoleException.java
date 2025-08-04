package org.rentfriend.exception;

public class BadRoleException extends RuntimeException{
  public BadRoleException() {
  }

  public BadRoleException(String message) {
    super(message);
  }

  public BadRoleException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRoleException(Throwable cause) {
    super(cause);
  }

  public BadRoleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
