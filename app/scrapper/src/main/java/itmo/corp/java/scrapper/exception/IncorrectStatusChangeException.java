package itmo.corp.java.scrapper.exception;

public class IncorrectStatusChangeException extends RuntimeException{

    public IncorrectStatusChangeException() {
    }

    public IncorrectStatusChangeException(String message) {
        super(message);
    }
}
