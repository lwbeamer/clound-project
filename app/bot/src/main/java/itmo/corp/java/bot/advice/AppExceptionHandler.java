package itmo.corp.java.bot.advice;

import java.util.Arrays;

import itmo.corp.java.bot.exceptions.ChatNotFoundException;
import itmo.corp.java.bot.exceptions.LinkIsNotRegisteredToChatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import itmo.corp.java.bot.dto.ApiErrorResponse;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler({LinkIsNotRegisteredToChatException.class, ChatNotFoundException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleNotFoundExceptions(RuntimeException exception) {
        return new ApiErrorResponse(
            "Error",
            HttpStatus.BAD_REQUEST.toString(),
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList().toArray(String[]::new)
        );
    }

}
