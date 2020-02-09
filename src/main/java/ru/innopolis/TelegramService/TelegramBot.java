package ru.innopolis.TelegramService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.innopolis.TelegramService.service.MessageReciever;
import ru.innopolis.TelegramService.service.MessageSender;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Класс TelegramBot
 * <p>
 * 06.02.2020
 *
 * @author Александр Коваленко
 */
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    @Value("#{systemEnvironment['SMART_TELEGRAM_TOKEN']}")
    private String telegramToken;

    public final Queue<BotApiMethod<Message>> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Update> receiveQueue = new ConcurrentLinkedQueue<>();

    public TelegramBot() {
        MessageSender messageSender = new MessageSender(this);
        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(1);
        sender.start();
        MessageReciever messageReciever = new MessageReciever(this);
        Thread reciever = new Thread(messageReciever);
        reciever.setDaemon(true);
        reciever.setName("MsgReciever");
        reciever.setPriority(3);
        reciever.start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Receive new Update. updateID: " + update.getUpdateId());
        receiveQueue.add(update);
    }

    /**
     * Произвольное text-notify
     *
     * @param notify
     * @param chatId
     */

    public void sendNotificationToChatId(String notify, Long chatId) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText(notify);
        sendQueue.add(message);
    }

    @Override
    public String getBotUsername() {
        return "PrettySmartHomeBot";
    }

    @Override
    public String getBotToken() {
     //   log.debug("Bot token: " + telegramToken);
        return telegramToken;
    }


}
