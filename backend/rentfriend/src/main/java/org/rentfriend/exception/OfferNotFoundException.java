package org.rentfriend.exception;

public class OfferNotFoundException extends RuntimeException{
  public OfferNotFoundException() {
  }

  public OfferNotFoundException(String message) {
    super(message);
  }

  public OfferNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public OfferNotFoundException(Throwable cause) {
    super(cause);
  }

  public OfferNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
