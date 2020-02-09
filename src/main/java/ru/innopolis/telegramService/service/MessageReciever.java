package ru.innopolis.telegramService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.innopolis.telegramService.TelegramBot;
import ru.innopolis.telegramService.exceptions.UserIdNotEqualsException;

import java.util.Collections;

/**
 * Класс MessageReciever
 * <p>
 * 09.02.2020
 *
 * @author Александр Коваленко
 */
public class MessageReciever implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageReciever.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    private TelegramBot bot;

    public MessageReciever(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgReciever.  Bot class: " + bot);
        while (true) {

            for (Update update = bot.receiveQueue.poll(); update != null; update = bot.receiveQueue.poll()) {
                log.debug("New object for analyze in queue " + update.toString());
                analyze(update);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    private void analyze(Update update) {
        log.debug("Update recieved: " + update.toString());
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            log.debug("Analyze Message :" + message);

            if (checkChatIdInAuthService(chatId))
                analyzeCommand(message);
            else
                try {
                    authorize(message);
                } catch (UserIdNotEqualsException e) {
                    e.printStackTrace();
                    log.warn("Unauthorized attemption, contact.userId is not equals with chat userId: " + message);
                    bot.sendNotificationToChatId("Произошла ошибка авторизации!", message.getChatId());
                }


        }

    }

    private void analyzeCommand(Message message) {
    }

    private boolean checkChatIdInAuthService(long chatId) {
        //todo Ищем пользователя по чат id
        return false;
    }

    private void authorize(Message message) throws UserIdNotEqualsException {
        if (message.hasText()) {
            requestUserPhone(message.getChatId());
        }
        if (message.hasContact()) {
            Contact contact = message.getContact();
            if (!contact.getUserID().equals(message.getFrom().getId())) {
                throw new UserIdNotEqualsException("Unauthorized attemption, contact.userId is not equals with chat userId: " + message);
            }
            String phoneNumber = contact.getPhoneNumber();
            log.debug("Получено соответствие номер телефона " + phoneNumber
                    + "и chatId: " + message.getChatId());
            //todo получить пользователя по номеру телефона в базе, сохранить chat id в пользователя authService
            setKeyboardForAuthorizedUser(message.getChatId());
        }

    }

    private void requestUserPhone(Long chatId) {
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
                .setText("Для использования бота необходимо авторизоваться, предоставив свой номер телефона.");
        setKeyboardForRequestContact(message);
        bot.sendQueue.add(message);
    }

    public void setKeyboardForRequestContact(SendMessage message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton().setText("Авторизоваться").setRequestContact(true));
        replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(replyKeyboardMarkup);
    }

    public void setKeyboardForAuthorizedUser(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Вы успешно авторизованы!");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton().setText("Список устройств"));
        keyboardRow.add(new KeyboardButton().setText("Статистика"));
        replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        message.setReplyMarkup(replyKeyboardMarkup);
        bot.sendQueue.add(message);
    }
}
