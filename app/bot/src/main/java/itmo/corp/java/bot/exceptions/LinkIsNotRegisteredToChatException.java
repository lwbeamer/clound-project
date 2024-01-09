package itmo.corp.java.bot.exceptions;

public class LinkIsNotRegisteredToChatException extends RuntimeException {


    public LinkIsNotRegisteredToChatException() {
    }

    public LinkIsNotRegisteredToChatException(String message) {
        super(message);
    }
}
