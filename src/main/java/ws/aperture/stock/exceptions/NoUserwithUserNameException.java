package ws.aperture.stock.exceptions;

public class NoUserwithUserNameException extends RuntimeException {
  public NoUserwithUserNameException(String userName) {
    super("No user with user name: " + userName + "\n");
  }
}
