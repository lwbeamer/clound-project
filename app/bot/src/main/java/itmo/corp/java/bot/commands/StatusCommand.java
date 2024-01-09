package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatusCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/status";
    }

    @Override
    public String description() {
        return "показать статус текущего заказа";
    }

    @Override
    public String handle(Update update) {
        try {
            return "Статус вашего заказа: " +
                scrapperClient.getStatus(update.message().chat().id()).status().toString();
        } catch (ScrapperClientException e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
    }
}
