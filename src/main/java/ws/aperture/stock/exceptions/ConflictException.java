package ws.aperture.stock.exceptions;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }
}
