package uz.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.bot.config.BotConfig;
import uz.bot.service.sender.ExecutorService;
import uz.bot.variable.entity.TelegramUser;
import uz.bot.variable.entity.UserBirthday;
import uz.bot.variable.message.base.GeneralSender;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static uz.bot.variable.constants.CommandMenu.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final ExecutorService executorService;

    private final TelegramService telegramService;

    @PostConstruct
    public void init() {
        try {
            List<BotCommand> list = new ArrayList<>();
            list.add(new BotCommand(START, "начальное меню"));
            execute(new SetMyCommands(list, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.warn("init warning : " + e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        GeneralSender sender = telegramService.onUpdate(update);
        if (sender != null) executeMessage(sender);

    }

    private void executeMessage(GeneralSender sender) {
        try {
            switch (sender.getType()) {
                case SEND_MESSAGE -> execute(executorService.send(sender));
                case EDIT_MESSAGE -> execute(executorService.edit(sender));
            }
        } catch (TelegramApiException e) {
            log.warn("Bot is not working: " + e);
        }
    }

    @Scheduled(/*cron = "${cron.every.day.at.1}"*/ fixedDelay = 5000)
    public void rememberBirthday() {
        try {
            log.info("rememberBirthday");
            List<TelegramUser> receiversUsersList = telegramService.receiversUsersList();
            List<UserBirthday> userBirthdaysList = telegramService.userBirthdaysList();
            for (TelegramUser receiversUser : receiversUsersList) {
                for (UserBirthday userBirthday : userBirthdaysList) {
                    int days = userBirthday.getBirthDate().getDayOfYear() - LocalDate.now().getDayOfYear();
                    if (days >= 0 && days <= 10) {
                        if (days == 0) {
                            execute(executorService.send(telegramService.birthday(receiversUser.getId(), userBirthday)));
                            execute(executorService.sendVoice(telegramService.sendVoice(receiversUser.getId())));
                        } else {
                            execute(executorService.send(telegramService.requestBirthday(receiversUser.getId(), userBirthday)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Remember Birthday : " + e);
        }
    }

}
