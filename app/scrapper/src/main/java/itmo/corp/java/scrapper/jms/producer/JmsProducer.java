package itmo.corp.java.scrapper.jms.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import itmo.corp.java.scrapper.jms.model.StatusMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsProducer {


    private final ObjectMapper objectMapper;
    @Qualifier("scrapperJmsTemplate")
    private final JmsTemplate jmsTemplate;

    public void send(StatusMessage msg) {
        try {
            String jsonObject = objectMapper.writeValueAsString(msg);
            jmsTemplate.convertAndSend(jsonObject);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

}
