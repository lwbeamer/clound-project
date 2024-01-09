package itmo.corp.java.scrapper.exception;

public class NotEnoughCapacityException extends RuntimeException {
    public NotEnoughCapacityException() {
    }

    public NotEnoughCapacityException(String message) {
        super(message);
    }
}
