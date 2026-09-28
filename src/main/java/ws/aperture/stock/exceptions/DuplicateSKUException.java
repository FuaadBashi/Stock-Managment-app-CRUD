package ws.aperture.stock.exceptions;

public class DuplicateSKUException extends RuntimeException {
    public DuplicateSKUException(Long productId, String SKU) {
        super("Duplicate SKU in system" + SKU + "for product ID" + productId + "\n");
    }
}
