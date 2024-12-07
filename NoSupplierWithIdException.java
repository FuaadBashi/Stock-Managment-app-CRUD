package ws.aperture.stock.exceptions;

public class NoSupplierWithIdException extends RuntimeException {
    public NoSupplierWithIdException(Long id) {
        super("No supplier with ID: " + id + "\n");
    }
}
