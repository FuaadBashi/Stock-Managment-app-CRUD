package ws.aperture.stock.exceptions;

public class DuplicateProductInOrderException extends RuntimeException {
    public DuplicateProductInOrderException(Long id) {
        super("Product id: " + id + ", contained multiple times in this order.\n");
    }
}
