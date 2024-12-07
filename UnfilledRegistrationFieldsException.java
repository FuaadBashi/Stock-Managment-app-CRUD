package ws.aperture.stock.exceptions;

public class UnfilledRegistrationFieldsException extends RuntimeException {
    public UnfilledRegistrationFieldsException( ) {
        super( "firstName, lastName, and email fields must all be nonempty.\n" );
    }
}
