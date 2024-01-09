package itmo.corp.java.bot.configuration;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import itmo.corp.java.bot.commands.CapacityCommand;
import itmo.corp.java.bot.commands.CommandsEnum;
import itmo.corp.java.bot.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import itmo.corp.java.bot.commands.AddCapacityCommand;
import itmo.corp.java.bot.commands.ChangeStatusCommand;
import itmo.corp.java.bot.commands.Command;
import itmo.corp.java.bot.commands.HelpCommand;
import itmo.corp.java.bot.commands.HistoryCommand;
import itmo.corp.java.bot.commands.OrderCommand;
import itmo.corp.java.bot.commands.StartCommand;
import itmo.corp.java.bot.commands.StatusCommand;
import itmo.corp.java.bot.dto.BotCommand;
import itmo.corp.java.bot.dto.SetCommandRequest;

@Configuration
@Slf4j
public class BotConfiguration {

    @Value("${tg.bot.token}")
    private String token;

    @Value("${tg.api.baseUrl}")
    private String tgApiBaseUrl;

    private final HelpCommand helpCommand;
    private final StartCommand startCommand;
    private final CapacityCommand capacityCommand;
    private final OrderCommand orderCommand;
    private final StatusCommand statusCommand;
    private final HistoryCommand historyCommand;
    private final AddCapacityCommand addCapacityCommand;
    private final ChangeStatusCommand changeStatusCommand;

    public BotConfiguration(
            HelpCommand helpCommand,
            StartCommand startCommand,
            CapacityCommand capacityCommand,
            OrderCommand orderCommand,
            StatusCommand statusCommand,
            HistoryCommand historyCommand,
            AddCapacityCommand addCapacityCommand,
            ChangeStatusCommand changeStatusCommand
    ) {
        this.helpCommand = helpCommand;
        this.startCommand = startCommand;
        this.capacityCommand = capacityCommand;
        this.orderCommand = orderCommand;
        this.statusCommand = statusCommand;
        this.historyCommand = historyCommand;
        this.addCapacityCommand = addCapacityCommand;
        this.changeStatusCommand = changeStatusCommand;
    }

    @Bean
    public Bot bot() {
        EnumMap<CommandsEnum, Command> commands = new EnumMap<>(CommandsEnum.class);

        commands.put(CommandsEnum.HELP, helpCommand);
        commands.put(CommandsEnum.CAPACITY, capacityCommand);
        commands.put(CommandsEnum.ORDER, orderCommand);
        commands.put(CommandsEnum.STATUS, statusCommand);
        commands.put(CommandsEnum.HISTORY, historyCommand);
        commands.put(CommandsEnum.ADD_CAPACITY, addCapacityCommand);
        commands.put(CommandsEnum.CHANGE_STATUS, changeStatusCommand);
        commands.put(CommandsEnum.START, startCommand);

        //Делаем кнопку "Меню" рядом с иконкой скрепки
        //нужно отправить запрос на API телеграмма (метод setMyCommands) со списком команд в виде List<BotCommand>
        List<BotCommand> apiCommands = new ArrayList<>(commands.values().stream().map(Command::toApiCommand).toList());
        WebClient botConfClient = WebClient.create(tgApiBaseUrl + token);
        botConfClient.post().uri("/setMyCommands").bodyValue(new SetCommandRequest(apiCommands)).exchangeToMono(r -> {
            if (!r.statusCode().equals(HttpStatus.OK)) {
                log.warn("Похоже, что API Telegram недоступен(");
            }
            return Mono.empty();
        }).block();

        return new Bot(token, commands);
    }

}
