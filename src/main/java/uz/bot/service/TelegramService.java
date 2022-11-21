package uz.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.bot.service.sender.CallBackQueryService;
import uz.bot.service.sender.MessageService;
import uz.bot.service.user.UserCache;
import uz.bot.variable.entity.TelegramUser;
import uz.bot.variable.entity.UserBirthday;
import uz.bot.variable.enums.UpdateEnum;
import uz.bot.variable.message.base.GeneralSender;
import uz.bot.variable.message.SenderMessage;
import uz.bot.repository.TelegramUserRepository;
import uz.bot.repository.UserBirthdayRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static uz.bot.variable.enums.UpdateEnum.*;
import static uz.bot.utils.Utils.toFormatDDMMYYYY;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramService {

    private final MessageService messageService;

    private final CallBackQueryService callBackQueryService;

    private final UserCache userCache;

    private final TelegramUserRepository telegramUserRepository;

    private final UserBirthdayRepository userBirthdayRepository;

    public GeneralSender onUpdate(Update update) {
        UpdateEnum updateEnum = getUpdate(update);

        switch (updateEnum) {
            case MESSAGE_TEXT -> {
                return messageText(update.getMessage());
            }
            case CALL_BACK_QUERY -> {
                return callBack(update.getCallbackQuery());
            }
            case DEFAULT_UPDATE -> {
                return null;
            }
        }

        return null;
    }

    private GeneralSender callBack(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        userCache.setMessageId(chatId, messageId);

        return callBackQueryService.start(chatId, callbackQuery);
    }

    private GeneralSender messageText(Message message) {
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        userCache.setMessageId(chatId, messageId);

        return messageService.start(chatId, message);
    }

    private UpdateEnum getUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) return MESSAGE_TEXT;
            if (message.hasVoice()) return VOICE_MESSAGE;
            if (message.hasPhoto()) return PHOTO_MESSAGE;
            if (message.hasDocument()) return DOCUMENT_MESSAGE;
            if (message.hasContact()) return CONTACT_MESSAGE;
            if (message.hasAnimation()) return ANIMATION_MESSAGE;
            if (message.hasAudio()) return AUDIO_MESSAGE;
            if (message.hasLocation()) return LOCATION_MESSAGE;
        }
        if (update.hasCallbackQuery()) return CALL_BACK_QUERY;
        return DEFAULT_UPDATE;
    }

    public GeneralSender requestBirthday(Long chatId, UserBirthday userBirthday) {
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(birthdayData(userBirthday))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String birthdayData(UserBirthday userBirthday) {
        String text = """
                <b>Скоро у</b> <code>%s</code> <b>день рождение! 🎉</b>
                                
                <b>Дата рождения</b>  :  <code>%s</code>
                                
                <b>Осталось</b> <code>%s</code> <b>дня</b>
                <b>Ему исполняется</b> <code>%s</code> лет
                """;
        return text.formatted(userBirthday.getName() + " " + userBirthday.getSurname(),
                toFormatDDMMYYYY(userBirthday.getBirthDate()),
                String.valueOf(userBirthday.getBirthDate().getDayOfYear() - LocalDate.now().getDayOfYear()),
                String.valueOf(Period.between(userBirthday.getBirthDate(), LocalDate.now()).getYears() + 1));
    }

    public GeneralSender birthday(Long chatId, UserBirthday userBirthday) {
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(today(userBirthday))
                .parseMode(ParseMode.HTML)
                .build();
    }

    private String today(UserBirthday userBirthday) {
        String text = """
                <b>У</b> <code>%s</code> <b>сегодня день рождение! 🎉</b>
                                
                <b>Дата рождения</b>  :  <code>%s</code>
                                
                <b>Ему исполнилось</b> <code>%s</code> <b>лет 🎂</b>
                """;
        return text.formatted(
                userBirthday.getName() + " " + userBirthday.getSurname(),
                toFormatDDMMYYYY(userBirthday.getBirthDate()),
                String.valueOf(Period.between(userBirthday.getBirthDate(), LocalDate.now()).getYears()));
    }

    public List<TelegramUser> receiversUsersList() {
        return telegramUserRepository.getReceiversList();
    }

    public List<UserBirthday> userBirthdaysList() {
        return userBirthdayRepository.findWhereDeletedFalse();
    }

    public GeneralSender sendVoice(Long chatId) {
        return messageService.sendVoice(chatId);
    }

}
