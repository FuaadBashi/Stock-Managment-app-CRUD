package ws.aperture.stock.exceptions;

public class NoUserWithIdException extends RuntimeException {
    public NoUserWithIdException(Long id) {
        super("No user with ID: " + id + "\n");
    }
}
