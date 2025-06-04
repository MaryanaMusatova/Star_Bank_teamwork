package com.example.Bank_Star.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.example.Bank_Star.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TelegramBotController extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    @Autowired
    private RecommendationService recommendationService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/recommend")) {
                String username = update.getMessage().getFrom().getUserName();
                String recommendations = recommendationService.getRecommendations(username);
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText(recommendations);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    logger.error("Error sending message", e);
                }
            } else {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText("Привет! Введите /recommend для получения рекомендаций.");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    logger.error("Error sending message", e);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "@anvarkartakaevBot";
    }

    @Override
    public String getBotToken() {
        return "8147749676:AAGVLhjVTFmORaRrqKxKVN8cW8j_nWUSMCQ";
    }
}