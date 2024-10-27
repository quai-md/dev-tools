package com.nu.art.pipeline.exceptions;

public class MUSTNeverHappenException
  extends Exception {

  MUSTNeverHappenException() {
  }

  MUSTNeverHappenException(String message) {
    super(message)
  }

  MUSTNeverHappenException(String message, Throwable cause) {
    super(message, cause)
  }

  MUSTNeverHappenException(Throwable cause) {
    super(cause)
  }
}
