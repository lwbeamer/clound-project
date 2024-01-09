package itmo.corp.java.bot.exceptions;

public class ChatNotFoundException extends RuntimeException {


    public ChatNotFoundException() {
    }

    public ChatNotFoundException(String message) {
        super(message);
    }
}
