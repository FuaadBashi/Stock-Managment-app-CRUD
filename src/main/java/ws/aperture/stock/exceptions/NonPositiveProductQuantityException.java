package ws.aperture.stock.exceptions;

public class NonPositiveProductQuantityException extends RuntimeException {
    public NonPositiveProductQuantityException(Long productId, int quantity) {
        super("Product with ID: " + productId + " has non-positive quantity: " + quantity);
    }
}
