package ru.innopolis.telegramService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;


@SpringBootApplication
public class TelegramServiceApp {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TelegramServiceApp.class, args);
    }
}
