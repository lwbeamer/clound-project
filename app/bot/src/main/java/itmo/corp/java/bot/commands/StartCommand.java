package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import itmo.corp.java.bot.dto.UserAddDto;

@Component
@Slf4j
public class StartCommand implements Command {

    private final ScrapperClient scrapperClient;

    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Value("${tg.bot.token}")
    private String token;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "зарегистрировать пользователя";
    }

    @Override
    public String handle(Update update) {
        long chatId = update.message().chat().id();
        try {
            UserAddDto userAddDto = new UserAddDto(update.message().chat().username(),
                update.message().chat().firstName(),
                update.message().chat().lastName()
            );
            scrapperClient.registerChat(chatId, userAddDto);
            return "Привет! Рад познакомиться, " + update.message().chat().firstName();
        } catch (ScrapperClientException e) {
            log.info(e.getMessage());
            return e.getMessage();
        }

    }
}
