package uz.bot.service.user;

import org.springframework.stereotype.Component;
import uz.bot.variable.entity.TelegramUser;
import uz.bot.variable.entity.UserBirthday;

import java.util.HashMap;
import java.util.Map;

import static uz.bot.variable.constants.UserStep.STEP_START;

@Component
public class UserCache {

    private final Map<Long, Integer> mapMessageId = new HashMap<>();

    private final Map<Long, String> mapStep = new HashMap<>();

    private final Map<Long, TelegramUser> mapUser = new HashMap<>();

    private final Map<Long, UserBirthday> mapUserBirthday = new HashMap<>();

    public void setMessageId(Long chatId, Integer messageId) {
        mapMessageId.put(chatId, messageId);
    }

    public Integer getMessageId(Long chatId) {
        return mapMessageId.get(chatId);
    }

    public void setStep(Long chatId, String step) {
        mapStep.put(chatId, step);
    }

    public String getStep(Long chatId) {
        mapStep.putIfAbsent(chatId, STEP_START);
        return mapStep.get(chatId);
    }

    public void setUser(Long chatId, TelegramUser user) {
        mapUser.put(chatId, user);
    }

    public TelegramUser getUser(Long chatId) {
        return mapUser.get(chatId);
    }

    public void setUserBirthday(Long chatId, UserBirthday user) {
        mapUserBirthday.put(chatId, user);
    }

    public UserBirthday getUserBirthday(Long chatId) {
//        mapUserBirthday.putIfAbsent(chatId, new UserBirthday(chatId));
        return mapUserBirthday.get(chatId);
    }
}
