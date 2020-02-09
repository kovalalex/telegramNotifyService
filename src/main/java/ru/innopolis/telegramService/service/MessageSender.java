package ru.innopolis.telegramService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.innopolis.telegramService.TelegramBot;

/**
 * Класс MessageSender
 * 09.02.2020
 *
 * @author Александр Коваленко
 */

public class MessageSender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private final int SENDER_SLEEP_TIME = 1000;
    private TelegramBot bot;

    public MessageSender(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgSender.  Bot class: " + bot);
        while (true) {
            for (BotApiMethod<Message> message = bot.sendQueue.poll();
                 message != null; message = bot.sendQueue.poll()) {
                log.debug("Get new msg to send " + message);
                try {
                    log.debug("Use Execute for " + message);
                    bot.execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(SENDER_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
