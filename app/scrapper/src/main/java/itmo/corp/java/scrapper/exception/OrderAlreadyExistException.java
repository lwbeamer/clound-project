package itmo.corp.java.scrapper.exception;

public class OrderAlreadyExistException extends RuntimeException {

    public OrderAlreadyExistException() {
    }

    public OrderAlreadyExistException(String message) {
        super(message);
    }
}
