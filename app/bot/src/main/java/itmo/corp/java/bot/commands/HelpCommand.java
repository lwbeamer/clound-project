package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {
    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "вывести окно с командами";
    }

    //У команды help (только у неё, это особый случай), не вызвается этот метод.
    //Она обрабатывается прямиком в UserMessageProcessor
    @Override
    public String handle(Update update) {
        return "Help is executing...";
    }
}
