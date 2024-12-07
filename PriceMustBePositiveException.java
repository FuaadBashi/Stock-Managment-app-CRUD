package ws.aperture.stock.exceptions;

public class PriceMustBePositiveException extends RuntimeException {
    public PriceMustBePositiveException() {
        super( "The retail price of a product must be greater than 0.00" );
    }   
}
