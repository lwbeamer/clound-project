package itmo.corp.java.scrapper.service.contract;

import itmo.corp.java.scrapper.model.commonDto.User;

public interface TgChatService {

    void register(User user);

    void unregister(Long chatId);

}
