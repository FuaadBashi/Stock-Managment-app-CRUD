package ws.aperture.stock.exceptions;

public class UnfilledProductFieldsException extends RuntimeException {
    public UnfilledProductFieldsException() {
        super("Name, Supplier Id and SKU must all be nonempty.\n");
    }
}
