package itmo.corp.java.bot.telegram;

import com.pengrad.telegrambot.model.Update;
import itmo.corp.java.bot.commands.CommandsEnum;
import itmo.corp.java.bot.commands.AddCapacityCommand;
import itmo.corp.java.bot.commands.ChangeStatusCommand;
import itmo.corp.java.bot.commands.Command;
import itmo.corp.java.bot.commands.HelpCommand;
import itmo.corp.java.bot.commands.OrderCommand;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class UserMessageProcessor {

//    private final List<Command> commands;

    // Состояние пользователя. Нужно для удобной работы с командами /track и /untrack
    // TYPING_COMMAND - состояние ввода команды (по-умолчанию)
    // TYPING_TRACKED - состояние ввода ссылки для добавления
    // TYPING_UNTRACKED - состояние ввода ссылки для удаления
    private final Map<Long, UserState> userStateMap;

    private final EnumMap<CommandsEnum, Command> commands;
    private final Map<Long, Update> userOrderMap;

    public UserMessageProcessor(EnumMap<CommandsEnum, Command> commands) {
        this.commands = commands;
        userStateMap = new HashMap<>();
        userOrderMap = new HashMap<>();
    }

    private final long adminId = 1924505648;
    private final int stirkaPrice = 80;
    private final int stirkaAndSushkaPrice = 120;
    private final int himchistkaPrice = 150;


    public String process(Update update) {
        Command command;
        Long chatId = update.message().chat().id();
        userStateMap.putIfAbsent(chatId, UserState.TYPING_COMMAND);
        switch (userStateMap.get(chatId)) {
            case TYPING_CAPACITY -> {
                userStateMap.put(chatId, UserState.TYPING_COMMAND);
                return commands.get(CommandsEnum.valueOfLabel("/addcap")).handle(update);
            }
            case TYPING_STATUS -> {
                userStateMap.put(chatId, UserState.TYPING_COMMAND);
                return commands.get(CommandsEnum.valueOfLabel("/changestat")).handle(update);
            }
            case TYPING_ORDER -> {
                userOrderMap.put(chatId, update);
                String confMsg = confirmationMessage(update);
                if (confMsg.endsWith("!")){
                    userStateMap.put(chatId, UserState.TYPING_COMMAND);
                } else {
                    userStateMap.put(chatId, UserState.CONFIRM_ORDER);
                }
                return confMsg;
            }
            case CONFIRM_ORDER -> {
                userStateMap.put(chatId, UserState.TYPING_COMMAND);
                String msg = update.message().text();
                if ("y".equals(msg)){
                    return commands.get(CommandsEnum.valueOfLabel("/order")).handle(userOrderMap.get(chatId));
                } else if ("n".equals(msg)) {
                    return "Заказ не подтверждён";
                } else {
                    return "Некорректный ответ, пожалуйста, попробуйте ещё раз!";
                }
            }
            case TYPING_COMMAND -> {
                userStateMap.put(chatId, UserState.TYPING_COMMAND);
                command = commands.get(CommandsEnum.valueOfLabel(update.message().text()));
                if (command == null) {
                    return "Неизвестная команда. Нажмите 'Меню' чтобы посмотреть список доступных команд";
                }
                if (command instanceof AddCapacityCommand) {
                    if (chatId != adminId) return "У вас нет доступа к этой команде!";
                    userStateMap.put(chatId, UserState.TYPING_CAPACITY);
                    return "Введите кол-во мест, которые хотите добавить";
                }
                if (command instanceof ChangeStatusCommand) {
                    if (chatId != adminId) return "У вас нет доступа к этой команде!";
                    userStateMap.put(chatId, UserState.TYPING_STATUS);
                    return """
                            Введите сообщение в таком формате:
                            *order_id* *status_number*
                            где status_number - одно из трёх значений:
                            1 - CONFIRMED
                            2 - CANCELLED
                            3 - DONE""";
                }
                if (command instanceof OrderCommand) {
                    userStateMap.put(update.message().chat().id(), UserState.TYPING_ORDER);
                    return """
                            Введите сообщение в таком формате:
                            *кол-во пакетов*
                            *номер услуги*
                            Для нескольких пакетов номер услуги вводится через пробел
                            и представляет одно из трёх значений:
                            1 - постирать (%dр)
                            2 - постирать + посушить (%dp)
                            3 - химчистка (%dp)""".formatted(stirkaPrice, stirkaAndSushkaPrice, himchistkaPrice);
                }
                if (command instanceof HelpCommand) {
                    StringBuilder text = new StringBuilder();
                    for (Command c : commands.values()) {
                        text.append(c.command()).append(" - ").append(c.description()).append("\n");
                    }
                    return text.toString();
                }
                return command.handle(update);
            }
            default -> {
                //По логике этот return никак недостижим
                return "Что-то пошло не так. Проблема на нашей стороне";

            }
        }

    }

    private String confirmationMessage(Update update) {
        String msg = update.message().text();
        try {
            String[] firstSplit = msg.split("\n");
            if (firstSplit.length != 2) return "Сообщение с заказом должно представлять две строки!";
            firstSplit[0] = firstSplit[0].trim();
            firstSplit[1] = firstSplit[1].trim();
            if (firstSplit[0].length() != 1) return "Число пакетов - одно число!";
            int packsCount = Integer.parseInt(firstSplit[0]);
            if (packsCount < 0 || packsCount > 3) return "Число пакетов от 1 до 3 на человека!";
            String[] secondSplit = firstSplit[1].split(" ");
            if (secondSplit.length != packsCount) return "Кол-во пакетов не соответствует кол-ву услуг!";
            for (String s : secondSplit) {
                if (!("1".equals(s) || "2".equals(s) || "3".equals(s))) return "Некорректные номера услуг!";
            }
            StringBuilder builder = new StringBuilder("Ваш заказ\n");
            builder.append("Количество пакетов: ").append(packsCount).append("\n");
            int totalCost = 0;
            for (int i = 0; i < packsCount; i++) {
                builder.append("Услуга для пакета номер %d: ".formatted(i+1));
                switch (secondSplit[i]) {
                    case "1" -> {
                        builder.append("стирка\n");
                        totalCost += stirkaPrice;
                    }
                    case "2" -> {
                        builder.append("стирка + сушка\n");
                        totalCost += stirkaAndSushkaPrice;
                    }
                    case "3" -> {
                        builder.append("химчистка\n");
                        totalCost += himchistkaPrice;
                    }
                }
            }
            builder.append("Итоговая стоимость: %dр\n".formatted(totalCost));
            builder.append("Оформить заказ? y/n");
            return builder.toString();
        } catch (NumberFormatException e){
            return "Некорректные числа в номерах услуг или кол-ве пакетов!";
        }
    }

}
