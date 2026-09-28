package ws.aperture.stock.exceptions;

public class NoCustomerOrderIdException extends RuntimeException {
  public NoCustomerOrderIdException(Long id) {
    super("No Customer Order with ID: " + id + "\n");
  }
}
