package itmo.corp.java.scrapper.exception;

public class UnchangeableStatusException extends RuntimeException {

    public UnchangeableStatusException() {
    }

    public UnchangeableStatusException(String message) {
        super(message);
    }
}
