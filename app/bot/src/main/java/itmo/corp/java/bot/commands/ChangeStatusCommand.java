package itmo.corp.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.client.ScrapperClient;
import itmo.corp.java.bot.client.ScrapperClientException;
import itmo.corp.java.bot.dto.Status;
import itmo.corp.java.bot.dto.StatusRequestAndResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeStatusCommand implements Command{

    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return "/changestat";
    }

    @Override
    public String description() {
        return "изменить статус заказа по ID";
    }

    @Override
    public String handle(Update update) {
        try {
            String msg = update.message().text();
            if (!validateMessage(msg)) return "Некорректно сформировано сообщение! "
                + "Пожалуйста, попробуйте еще раз";
            scrapperClient.changeStatus(getOrderIdByMsg(msg), new StatusRequestAndResponse(getStatusByMsg(msg)));
            return "Статус заказа успешно изменён";
        } catch (ScrapperClientException e) {
            log.info(e.getMessage());
            return e.getMessage();
        }
    }


    private boolean validateMessage(String msg) {
        try {
            String[] splittedMsg = msg.split(" ");
            if (splittedMsg.length != 2) return false;
            Long.parseLong(splittedMsg[0]);
            if (!(splittedMsg[1].equals("1") || splittedMsg[1].equals("2") || splittedMsg[1].equals("3"))) return false;
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    private Status getStatusByMsg(String msg) {
        String[] splittedMsg = msg.split(" ");
        if (splittedMsg[1].equals("1")) return Status.CONFIRMED;
        else if (splittedMsg[1].equals("2")) return Status.CANCELLED;
        else return Status.DONE;
    }

    private long getOrderIdByMsg(String msg) {
        String[] splittedMsg = msg.split(" ");
        return Long.parseLong(splittedMsg[0]);
    }
}
