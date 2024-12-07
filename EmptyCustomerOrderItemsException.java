package ws.aperture.stock.exceptions;

public class EmptyCustomerOrderItemsException extends RuntimeException {
    public EmptyCustomerOrderItemsException() {
        super( "Customer order request must not be empty\n");
    }
}
