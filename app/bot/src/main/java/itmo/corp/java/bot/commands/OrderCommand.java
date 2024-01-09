package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import itmo.corp.java.bot.dto.OrderRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/order";
    }
    @Override
    public String description() {
        return "совершить заказ";
    }

    @Override
    public String handle(Update update) {
        try {
            String msg = update.message().text();
            String[] splittedMsg = msg.split("\n");
            int packsCount = Integer.parseInt(splittedMsg[0]);
            scrapperClient.createOrder(new OrderRequest(update.message().chat().id(), packsCount));
            return "Заказ успешно создан в статусе NEW и ожидает оплаты!";
        } catch (ScrapperClientException | NumberFormatException e) {
            if (e instanceof ScrapperClientException) {
                log.info(e.getMessage());
                return e.getMessage();
            }
            else return "Что-то пошло не так, повторите, пожалуйста, попытку позже!";
        }
    }
}
