package ru.innopolis.telegramService.exceptions;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс UserIdNotEqualsException
 * <p>
 * 09.02.2020
 *
 * @author Александр Коваленко
 */
public class UserIdNotEqualsException extends TelegramApiException {
    public UserIdNotEqualsException(String s) {
        super(s);
    }
}
