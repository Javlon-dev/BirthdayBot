package uz.bot.service.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.bot.repository.UserBirthdayRepository;
import uz.bot.service.keyboard.InlineKeyboardService;
import uz.bot.service.keyboard.ReplyKeyboardService;
import uz.bot.service.user.UserCache;
import uz.bot.variable.entity.TelegramUser;
import uz.bot.variable.message.SenderMessage;
import uz.bot.variable.message.base.GeneralSender;
import uz.bot.repository.TelegramUserRepository;

import java.io.*;
import java.lang.reflect.Field;

import static uz.bot.utils.Utils.toFormatDDMMYYYY;
import static uz.bot.variable.constants.UserMenu.*;

@Slf4j
abstract public class AbstractService {

    @Value("${admin.chat.id}")
    protected Long adminChatId;

    @Autowired
    protected UserCache userCache;

    @Autowired
    protected InlineKeyboardService inline;

    @Autowired
    protected ReplyKeyboardService reply;

    @Autowired
    protected TelegramUserRepository telegramUserRepository;

    @Autowired
    protected UserBirthdayRepository userBirthdayRepository;

    protected InputFile getInputFileByPath(String path, String fileName, String ext) {
        byte[] imageInByte;
        String file = fileName + "." + ext;
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path + file))) {
            imageInByte = in.readAllBytes();
            InputStream is = new ByteArrayInputStream(imageInByte);
            InputFile inputFile = new InputFile();
            return inputFile.setMedia(is, file);
        } catch (IOException e) {
            log.warn("getInputFileByPath : " + e);
            throw new RuntimeException(e);
        }
    }

    protected boolean isValidMessage(String text, Class<?> type) {
        try {
            for (Field declaredField : type.getDeclaredFields()) {
                if (text.equals(declaredField.get(declaredField.getName()).toString())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("isValidMessage : " + e);
            return false;
        }
    }

    protected TelegramUser getUser(User user) {
        TelegramUser tgUser = userCache.getUser(user.getId());
        if (tgUser == null) {
            tgUser = telegramUserRepository
                    .findById(user.getId())
                    .orElseGet(() -> telegramUserRepository.save(new TelegramUser(user)));
            userCache.setUser(user.getId(), tgUser);
        }
        return tgUser;
    }

    protected TelegramUser getUserFromRepo(Long chatId) {
        return telegramUserRepository
                .findById(chatId)
                .orElseThrow();
    }

    protected GeneralSender sendMessage(Long chatId, String text) {
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    protected GeneralSender startMenu(Long chatId, String text) {
        if (adminChatId.equals(chatId)) {
            return SenderMessage
                    .builder()
                    .chatId(chatId)
                    .text(text)
                    .reply(reply.getKeyboard(
                            reply.getRows(
                                    reply.getButton(ADD_DATA)),
                            reply.getRows(
                                    reply.getButton(GET_DATA)),
                            reply.getRows(
                                    reply.getButton(MODIFYING_USER))))
                    .build();
        } else {
            return SenderMessage
                    .builder()
                    .chatId(chatId)
                    .text(text)
                    .reply(reply.getKeyboard(
                            reply.getRows(
                                    reply.getButton(ADD_DATA)),
                            reply.getRows(
                                    reply.getButton(GET_DATA))))
                    .build();
        }
    }

    protected GeneralSender getDataMenu(Long chatId, String text) {
        ReplyKeyboardMarkup keyboard =
                reply.getKeyboard(
                        reply.getRows(
                                reply.getButton(DELETE_DATA)),
                        reply.getRows(
                                reply.getButton(BACK)));
        keyboard.setOneTimeKeyboard(true);
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .reply(keyboard)
                .build();
    }

    protected GeneralSender addDataMenu(Long chatId, String text) {
        ReplyKeyboardMarkup keyboard =
                reply.getKeyboard(
                        reply.getRows(
                                reply.getButton(CANCEL),
                                reply.getButton(ACCEPT)));
        keyboard.setOneTimeKeyboard(true);
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .reply(keyboard)
                .build();
    }

    protected GeneralSender modifyingMenu(Long chatId, String text) {
        ReplyKeyboardMarkup keyboard =
                reply.getKeyboard(
                        reply.getRows(
                                reply.getButton(BACK)));
        keyboard.setOneTimeKeyboard(true);
        return SenderMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .reply(keyboard)
                .build();
    }

}
