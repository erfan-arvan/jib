package com.google.cloud.tools.jib.image;
public class LayerCountMismatchException extends Exception {
  public LayerCountMismatchException(String message) {
    super(message);
  }
}
