package uz.bot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.bot.service.base.AbstractService;
import uz.bot.variable.constants.UserMenu;
import uz.bot.variable.entity.TelegramUser;
import uz.bot.variable.entity.UserBirthday;
import uz.bot.variable.message.SenderVoice;
import uz.bot.variable.message.base.GeneralSender;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static uz.bot.variable.constants.UserMenu.*;
import static uz.bot.variable.constants.CommandMenu.*;
import static uz.bot.variable.constants.UserStep.*;
import static uz.bot.utils.Utils.*;

@Service
@Slf4j
public class MessageService extends AbstractService {

    public GeneralSender start(Long chatId, Message message) {
        GeneralSender command = menuCommand(chatId, message);
        if (command != null) {
            return command;
        }

        if (isValidMessage(message.getText(), UserMenu.class)) {
            return userMenu(chatId, message);
        } else {
            return stepMenu(chatId, message);
        }
    }

    private GeneralSender userMenu(Long chatId, Message message) {
        TelegramUser telegramUser = getUser(message.getFrom());
        switch (message.getText()) {
            case ADD_DATA -> {
                if (telegramUser.isHasRead() && telegramUser.isHasWrite()) {
                    userCache.setStep(chatId, STEP_ADD_DATA);
                    return stepMenu(chatId, message);
                } else {
                    return startMenu(chatId, "*У вас нет доступа*");
                }
            }
            case GET_DATA -> {
                if (telegramUser.isHasRead()) {
                    userCache.setStep(chatId, STEP_GET_DATA);
                    return dataMenu(chatId, null);
                } else {
                    return startMenu(chatId, "*У вас нет доступа*");
                }
            }
            case DELETE_DATA -> {
                if (telegramUser.isHasRead() && telegramUser.isHasWrite()) {
                    userCache.setStep(chatId, STEP_CHECKING_DATA);
                    return stepMenu(chatId, message);
                } else {
                    return startMenu(chatId, "*У вас нет доступа*");
                }
            }
            case BACK -> {
                return startMenu(chatId, "*С чего начнём*");
            }
            case ACCEPT -> {
                if (telegramUser.isHasRead() && telegramUser.isHasWrite()) {
                    return addData(chatId);
                } else {
                    return startMenu(chatId, "*У вас нет доступа*");
                }
            }
            case CANCEL -> {
                return startMenu(chatId, "*С чего начнём*");
            }
            case MODIFYING_USER -> {
                if (adminChatId.equals(chatId)) {
                    userCache.setStep(chatId, STEP_MODIFYING_USER);
                    return stepMenu(chatId, message);
                } else {
                    return startMenu(chatId, "*У вас нет доступа*");
                }
            }
            default -> {
                return startMenu(chatId, "*С чего начнём*");
            }
        }

    }

    private GeneralSender menuCommand(Long chatId, Message message) {
        switch (message.getText()) {
            case START -> {
                getUser(message.getFrom());
                return startMenu(chatId, "*Добро пожаловать !*");
            }
            default -> {
                return null;
            }
        }
    }

    private GeneralSender stepMenu(Long chatId, Message message) {
        switch (userCache.getStep(chatId)) {
            case STEP_START -> {
                getUser(message.getFrom());
                return startMenu(chatId, "*Добро пожаловать !*");
            }
            case STEP_ADD_DATA -> {
                userCache.setStep(chatId, STEP_SET_NAME);
                userCache.setUserBirthday(chatId, new UserBirthday(chatId));
                return sendMessage(chatId, "*Введите имя*");
            }
            case STEP_SET_NAME -> {
                userCache.setStep(chatId, STEP_SET_SURNAME);
                UserBirthday userBirthday = userCache.getUserBirthday(chatId);
                userBirthday.setName(message.getText());
                userCache.setUserBirthday(chatId, userBirthday);
                return sendMessage(chatId, "*Введите фамилию*");
            }
            case STEP_SET_SURNAME -> {
                userCache.setStep(chatId, STEP_SET_BIRTHDATE);
                UserBirthday userBirthday = userCache.getUserBirthday(chatId);
                userBirthday.setSurname(message.getText());
                userCache.setUserBirthday(chatId, userBirthday);
                return sendMessage(chatId, "*Введите дату рождения\n" +
                        "Например :* `%s`".formatted(toFormatDDMMYYYY(LocalDate.now())));
            }
            case STEP_SET_BIRTHDATE -> {
                if (checkPatternDate(message.getText())) {
                    return sendMessage(chatId, "*Не правильная дата\n" +
                            "Введите дату рождение повторно !*");
                }
                if (checkDate(message.getText())) {
                    return sendMessage(chatId, "*Серьёзно ?\n" +
                            "Введите дату рождение повторно !*");
                }
                userCache.setStep(chatId, STEP_GET_DATA);
                UserBirthday userBirthday = userCache.getUserBirthday(chatId);
                userBirthday.setBirthDate(toFormatDDMMYYYY(message.getText()));
                userCache.setUserBirthday(chatId, userBirthday);
                return addDataMenu(chatId, checkingData(userCache.getUserBirthday(chatId)));
            }
            case STEP_GET_DATA -> {
                return getData(chatId, message);
            }
            case STEP_CHECKING_DATA -> {
                userCache.setStep(chatId, STEP_DELETE_DATA);
                return getDataMenu(chatId, "*Введите ID !*");
            }
            case STEP_DELETE_DATA -> {
                if (!checkIsNumber(message.getText())) {
                    return getDataMenu(chatId, "*Не правильный ID !*");
                }
                return deleteData(chatId, message);
            }
            case STEP_MODIFYING_USER -> {
                userCache.setStep(chatId, STEP_MODIFYING_DATA);
                return modifyingUser(chatId, "*Введите форму !*");
            }
            case STEP_MODIFYING_DATA -> {
                String[] split = message.getText().split(" ");
                if (split.length == 3) {
                    return modifyingData(chatId, split);
                } else {
                    return modifyingMenu(chatId, "*Не правильная форма !*");
                }
            }
            default -> {
                return startMenu(chatId, "`С чего начнём`");
            }
        }
    }

    private String checkingData(UserBirthday userBirthday) {
        String text = """
                *Имя*  :  `%s`
                *Фамилия*  :  `%s`
                *Дата рождения*  :  `%s`
                     
                *Сохранить ?*
                """;
        return text.formatted(userBirthday.getName(),
                userBirthday.getSurname(),
                toFormatDDMMYYYY(userBirthday.getBirthDate()));
    }

    private GeneralSender dataMenu(Long chatId, String text) {
        List<UserBirthday> userBirthdayList = userBirthdayRepository.findWhereDeletedFalse();
        StringBuilder builder = new StringBuilder();
        builder.append("*Информации не нашлось*\n\n");
        if (text != null) {
            builder.append(text);
        }
        if (userBirthdayList.isEmpty()) {
            return startMenu(chatId, builder.toString());
        }
        builder = startBuilder("ID", "Имя", "Фамилия", "Дата Рождения");
        for (UserBirthday userBirthday : userBirthdayList) {
            builder.append(addBirthdayUser(userBirthday));
        }
        builder.append("```\n");
        if (text != null) {
            builder.append(text);
        }
        return getDataMenu(chatId, builder.toString());
    }

    private String addBirthdayUser(UserBirthday userBirthday) {
        String text = """
                | %s | %s | %s | %s |
                """;
        return text.formatted(userBirthday.getId(),
                userBirthday.getName(),
                userBirthday.getSurname(),
                toFormatDDMMYYYY(userBirthday.getBirthDate()));
    }

    private GeneralSender deleteData(Long chatId, Message message) {
        Long id = Long.valueOf(message.getText());
        int updateHasDeleted = userBirthdayRepository.updateHasDeleted(chatId, id);
        if (updateHasDeleted == 0) {
            return getDataMenu(chatId, "*Не существующий ID* `%s`".formatted(message.getText()));
        } else {
            return dataMenu(chatId, "*Удалено ! ID* `%s`".formatted(message.getText()));
        }
    }


    private GeneralSender addData(Long chatId) {
        UserBirthday userBirthday = userCache.getUserBirthday(chatId);
        if (checkForNull(userBirthday.getName(), userBirthday.getSurname(), userBirthday.getBirthDate())) {
            return startMenu(chatId, "*Не правильная информация\n" +
                    "Пожалуйста введите заново !*");
        }
        UserBirthday save = userBirthdayRepository.save(userBirthday);
        return startMenu(chatId, "*Сохранено ! ID* `%s`".formatted(save.getId().toString()));
    }

    private GeneralSender getData(Long chatId, Message message) {
        List<UserBirthday> userBirthdayList = userBirthdayRepository.findByData(message.getText());
        if (userBirthdayList.isEmpty()) {
            return startMenu(chatId, "*Такого не нашлось* `%s`".formatted(message.getText()));
        }
        StringBuilder builder = startBuilder("ID", "Имя", "Фамилия", "Дата Рождения");
        for (UserBirthday userBirthday : userBirthdayList) {
            builder.append(addBirthdayUser(userBirthday));
        }
        builder.append("```\n");
        builder.append("*Вот что нашлось по* `%s`".formatted(message.getText()));
        return getDataMenu(chatId, builder.toString());
    }

    private StringBuilder startBuilder(Object... values) {
        String text = "```\n" + "| %s ".repeat(values.length) + "|\n";
        return new StringBuilder(text.formatted(values));
    }


    private GeneralSender modifyingUser(Long chatId, String text) {
        List<TelegramUser> telegramUsers = telegramUserRepository.findAll();
        StringBuilder builder = startBuilder("ID", "fullName", "hasRead", "hasWrite");
        for (TelegramUser telegramUser : telegramUsers) {
            builder.append(addInfoUser(telegramUser));
        }
        builder.append("```\n");
        builder.append(text);
        return modifyingMenu(chatId, builder.toString());
    }

    private String addInfoUser(TelegramUser telegramUser) {
        String text = """
                | %s | %s | %b | %b |
                """;
        return text.formatted(telegramUser.getId(),
                telegramUser.getUserName() + " " + telegramUser.getFirstName() + " " + telegramUser.getLastName(),
                telegramUser.isHasRead(),
                telegramUser.isHasWrite());
    }

    private GeneralSender modifyingData(Long chatId, String... values) {
        try {
            Long userId = Long.valueOf(values[0]);
            String field = values[1];
            boolean data = Boolean.parseBoolean(values[2]);

            if (field.equals("hasRead")) {
                telegramUserRepository.updateHasRead(userId, data);

            } else if (field.equals("hasWrite")) {
                telegramUserRepository.updateHasWrite(userId, data);

            }

            TelegramUser telegramUser = telegramUserRepository.findById(userId).orElseThrow();
            userCache.setUser(userId, telegramUser);

            StringBuilder builder = startBuilder("ID", "fullName", "hasRead", "hasWrite");
            builder.append(addInfoUser(telegramUser));
            builder.append("```");

            return modifyingMenu(chatId, builder.toString());
        } catch (Exception e) {
            log.warn("modifyingData : " + e);
            return modifyingMenu(chatId, e.toString());
        }
    }

    public GeneralSender sendVoice(Long chatId) {
        return SenderVoice
                .builder()
                .chatId(chatId)
                .caption("`Happy Birthday`")
                .inputFile(getInputFileByPath("/home/bot/", "birthday", "ogg"))
                .build();
    }
}
