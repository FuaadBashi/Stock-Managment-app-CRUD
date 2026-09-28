package ws.aperture.stock.exceptions;

public class NoProductWithIdException extends RuntimeException {
  public NoProductWithIdException(Long id) {
    super("No product with ID: " + id + "\n");
  }
}
