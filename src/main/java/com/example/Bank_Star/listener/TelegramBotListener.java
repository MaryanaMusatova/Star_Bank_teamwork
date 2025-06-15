package com.example.Bank_Star.listener;

import com.example.Bank_Star.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotListener extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotListener.class);

    @Autowired
    private RecommendationService recommendationService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/recommend ")) {
                String username = messageText.substring("/recommend ".length()).trim();
                try {
                    String response = recommendationService.getRecommendations(username);
                    sendMessage(chatId, response);
                } catch (Exception e) {
                    logger.error("Рекомендация по обработке ошибок", e);
                    sendMessage(chatId, "Произошла ошибка при обработке запроса");
                }
            } else if (messageText.equals("/start") || messageText.equals("/help")) {
                sendMessage(chatId, "Привет! Для получения рекомендаций введите /recommend username");
            } else {
                sendMessage(chatId, "Неизвестная команда. Введите /help для справки");
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка отправки сообщения в чат {}", chatId, e);
        }
    }

    @Override
    public String getBotUsername() {
        return "1111111111111111";
    }

    @Override
    public String getBotToken() {
        return "111111111111111111111";
    }
}