package ws.aperture.stock.exceptions;

public class NoIngredientWithIdException extends RuntimeException {
  public NoIngredientWithIdException(Long id) {
    super("No ingredient with ID: " + id + "\n");
  }
}
