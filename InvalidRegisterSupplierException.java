package ws.aperture.stock.exceptions;

public class InvalidRegisterSupplierException extends RuntimeException {
    public InvalidRegisterSupplierException() {
        super("companyName must not be nonempty and companyNumber must be 8 characters or more.\n");
    }
}
