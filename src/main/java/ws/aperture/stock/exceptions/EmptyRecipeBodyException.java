package ws.aperture.stock.exceptions;

public class EmptyRecipeBodyException extends RuntimeException {
    public EmptyRecipeBodyException() {
        super("Recipe items must not be empty\n");
    }
}
