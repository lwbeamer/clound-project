package itmo.corp.java.scrapper.service.jpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import itmo.corp.java.scrapper.exception.ChatAlreadyExistException;
import itmo.corp.java.scrapper.exception.ChatNotFoundException;
import itmo.corp.java.scrapper.model.commonDto.User;
import itmo.corp.java.scrapper.model.jpa.UserEntity;
import itmo.corp.java.scrapper.repository.UserRepository;
import itmo.corp.java.scrapper.service.contract.TgChatService;
import java.util.Optional;

@Slf4j
@Service
public class JpaTgChatServiceImpl implements TgChatService {

    private final UserRepository userRepository;

    public JpaTgChatServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(User user) {
        log.info("register() method invocation in JpaTgChatServiceImpl. User chatId = " + user.getChatId());
        Optional<UserEntity> optionalUser = userRepository.findById(user.getChatId());
        if (optionalUser.isPresent()) {
            throw new ChatAlreadyExistException("Такой чат уже зарегистрирован!");
        }
        userRepository.save(User.toEntity(user));
    }

    @Override
    public void unregister(Long chatId) {
        log.info("unregister() method invocation in JpaTgChatServiceImpl. User chatId = " + chatId);
        Optional<UserEntity> optionalUser = userRepository.findById(chatId);
        if (optionalUser.isEmpty()) {
            throw new ChatNotFoundException("Такой чат не зарегистрирован!");
        }
        userRepository.delete(optionalUser.get());
    }

}
