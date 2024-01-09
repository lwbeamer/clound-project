package itmo.corp.java.scrapper.exception;

public class DayNotFoundException extends RuntimeException {

    public DayNotFoundException() {
    }

    public DayNotFoundException(String message) {
        super(message);
    }
}
