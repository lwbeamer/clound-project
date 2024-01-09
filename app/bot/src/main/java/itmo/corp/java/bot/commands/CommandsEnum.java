package itmo.corp.java.bot.commands;

import java.util.HashMap;
import java.util.Map;

public enum CommandsEnum {

    START("/start"),
    HELP("/help"),
    HISTORY("/history"),
    CAPACITY("/capacity"),
    STATUS("/status"),
    ORDER("/order"),
    ADD_CAPACITY("/addcap"),
    CHANGE_STATUS("/changestat");

    private static final Map<String, CommandsEnum> BY_LABEL = new HashMap<>();

    //Эта мапа инициализируется один раз при старте приложения.
    //Чтобы быстро брать инстанс CommandEnum по строковому представлению команды
    static {
        for (CommandsEnum c : values()) {
            BY_LABEL.put(c.label, c);
        }
    }

    private final String label;

    CommandsEnum(String command) {
        this.label = command;
    }

    public static CommandsEnum valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

}
