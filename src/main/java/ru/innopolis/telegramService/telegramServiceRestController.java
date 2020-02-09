package ru.innopolis.telegramService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Класс telegramServiceRestController
 * <p>
 * 06.02.2020
 *
 * @author Александр Коваленко
 */
@RestController
public class telegramServiceRestController {
    public TelegramBot telegramBot;

    @Autowired
    public telegramServiceRestController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public void send(@RequestBody Map<String, String> payload) {
        telegramBot.sendNotificationToChatId(payload.get("msg"), Long.parseLong(payload.get("chatId")));
    }
}
