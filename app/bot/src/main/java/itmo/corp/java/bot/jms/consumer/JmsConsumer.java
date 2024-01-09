package itmo.corp.java.bot.jms.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itmo.corp.java.bot.telegram.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import itmo.corp.java.bot.jms.model.StatusMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsConsumer {
    private final ObjectMapper objectMapper;
    private final Bot bot;
    @JmsListener(destination = "${jms.queues.scrapper.name}")
    public void acceptMessage(String requestJSON) {
        log.info("Прочитано сообщение: " + requestJSON);
        try {
            StatusMessage model = objectMapper.readValue(requestJSON, StatusMessage.class);
            bot.sendMessage(model.getChatId(), model.getText());
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка во время парсинга сообщения!");
        }
    }
}
