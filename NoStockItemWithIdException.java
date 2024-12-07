package ws.aperture.stock.exceptions;

public class NoStockItemWithIdException extends RuntimeException {
    
    public NoStockItemWithIdException(Long id) {
        super("No Stock Item with ID: " + id + "\n");
    }
}
