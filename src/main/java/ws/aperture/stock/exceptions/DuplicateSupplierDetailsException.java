package ws.aperture.stock.exceptions;

import ws.aperture.stock.model.Supplier;

public class DuplicateSupplierDetailsException extends RuntimeException {
    public DuplicateSupplierDetailsException(String message, Supplier duplicate) {
        super(
                message
                        + "\n"
                        + "Supplier Name:   "
                        + duplicate.getName()
                        + "\n"
                        + "Company Number:  "
                        + duplicate.getCompanyNumber()
                        + "\n");
    }
}
