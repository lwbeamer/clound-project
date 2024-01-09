package itmo.corp.java.scrapper.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import itmo.corp.java.scrapper.dto.UserAddDto;
import itmo.corp.java.scrapper.model.commonDto.User;
import itmo.corp.java.scrapper.service.contract.TgChatService;

@RestController
@RequestMapping("/tg-chat")
@Slf4j
public class ChatRestController {

    private final TgChatService chatService;

    public ChatRestController(TgChatService chatService) {
        this.chatService = chatService;
    }

    //Здесь в тело запроса добавил UserDto, чтобы добавить поля username, firstName и пр.
    //id оставил в PathVariable, чтобы не менять способ обращения к endpoint'у
    @PostMapping(value = "{id}")
    public void registerChat(@PathVariable Long id, @RequestBody UserAddDto userAddDto) {
        log.info("--> POST /tg-chat/{id}");
        chatService.register(new User(id, userAddDto.username(), userAddDto.firstName(), userAddDto.lastName()));
    }

    @DeleteMapping(value = "{id}")
    public void deleteChat(@PathVariable Long id) {
        log.info("--> DELETE /tg-chat/{id}");
        chatService.unregister(id);
    }

}
