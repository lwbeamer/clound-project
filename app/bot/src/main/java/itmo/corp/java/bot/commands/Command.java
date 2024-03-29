package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.dto.BotCommand;

public interface Command {
    String command();

    String description();

    String handle(Update update);

    default boolean supports(Update update) {
        return update.message().text().equals(command());
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
