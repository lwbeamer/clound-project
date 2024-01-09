package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;
import itmo.corp.java.bot.dto.ListOrderResponse;
import itmo.corp.java.bot.dto.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HistoryCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/history";
    }

    @Override
    public String description() {
        return "просмотреть историю заказов";
    }

    @Override
    public String handle(Update update) {
        StringBuilder msg = new StringBuilder();
        try {
            ListOrderResponse response =  scrapperClient.getHistory(update.message().chat().id());
            msg.append("Всего заказов: ").append(response.size()).append("\n");
            for (Order order : response.orders()) {
                msg.append("---------------------------------------------------").append("\n")
                    .append("ID: ").append(order.getId()).append("\n")
                    .append("Кол-во пакетов: ").append(order.getPacksCount()).append("\n")
                    .append("Создан: ").append(order.getCreatedAt()).append("\n")
                    .append("Статус: ").append(order.getStatus()).append("\n");
            }
        } catch (ScrapperClientException e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
        return msg.toString();
    }
}
