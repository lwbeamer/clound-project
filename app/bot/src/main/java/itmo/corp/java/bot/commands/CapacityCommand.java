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
public class CapacityCommand implements Command{

    private final ScrapperClient scrapperClient;
    @Override
    public String command() {
        return "/capacity";
    }

    @Override
    public String description() {
        return "показать кол-во свободных мест на текущий момент";
    }

    @Override
    public String handle(Update update) {
        try {
            return scrapperClient.getCapacity().capacity().toString();
        } catch (ScrapperClientException e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
    }
}
