package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddCapacityCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/addcap";
    }

    @Override
    public String description() {
        return "добавить свободные места на сегодня";
    }

    @Override
    public String handle(Update update) {
        long capacity;
        try {
            capacity = Long.parseLong(update.message().text());
            if (capacity > 0 && capacity < 1000){
                scrapperClient.addCapacity(capacity);
                return "Вместимость на сегодня успешно расширена";
            } else {
                return "Увеличение вместимости возможно в рамках от 1 до 1000";
            }
        } catch (NumberFormatException | ScrapperClientException e) {
            if (e instanceof NumberFormatException) return "Кол-во мест должно быть представлено числом от 1 до 1000";
            log.info(e.getMessage());
            return e.getMessage();
        }
    }
}
