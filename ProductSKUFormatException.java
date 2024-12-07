package ws.aperture.stock.exceptions;

public class ProductSKUFormatException extends RuntimeException {
    public ProductSKUFormatException( ) {
        super( "SKU format is incorrect.\n" );
    }
}
