package org.rentfriend.exception;

public class ProfileNotFoundException extends RuntimeException{
  public ProfileNotFoundException() {
  }

  public ProfileNotFoundException(String message) {
    super(message);
  }

  public ProfileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProfileNotFoundException(Throwable cause) {
    super(cause);
  }

  public ProfileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
