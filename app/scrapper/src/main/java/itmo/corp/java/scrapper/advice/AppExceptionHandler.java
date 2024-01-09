package itmo.corp.java.scrapper.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import itmo.corp.java.scrapper.dto.ApiErrorResponse;
import itmo.corp.java.scrapper.exception.ChatAlreadyExistException;
import itmo.corp.java.scrapper.exception.ChatNotFoundException;
import itmo.corp.java.scrapper.exception.DayNotFoundException;
import itmo.corp.java.scrapper.exception.IncorrectStatusChangeException;
import itmo.corp.java.scrapper.exception.UnchangeableStatusException;
import itmo.corp.java.scrapper.exception.NotEnoughCapacityException;
import itmo.corp.java.scrapper.exception.OrderAlreadyExistException;
import itmo.corp.java.scrapper.exception.OrderNotFoundException;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler({ChatNotFoundException.class, DayNotFoundException.class,
        OrderNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundExceptions(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ApiErrorResponse(
            "Error",
            HttpStatus.NOT_FOUND.toString(),
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList().toArray(String[]::new)
        );
    }

    @ExceptionHandler({ChatAlreadyExistException.class, UnchangeableStatusException.class,
        OrderAlreadyExistException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestExceptions(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ApiErrorResponse(
            "Error",
            HttpStatus.BAD_REQUEST.toString(),
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList().toArray(String[]::new)
        );
    }

    @ExceptionHandler({NotEnoughCapacityException.class, IncorrectStatusChangeException.class})
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiErrorResponse handleUnprocessableEntityExceptions(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ApiErrorResponse(
            "Error",
            HttpStatus.UNPROCESSABLE_ENTITY.toString(),
            exception.getClass().getName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString).toList().toArray(String[]::new)
        );
    }
}
