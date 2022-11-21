package uz.bot.variable.message.base;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.bot.variable.enums.MessageType;

public interface GeneralSender {
    Long getChatId();

    default String getText() {
        return null;
    }

    default String getParseMode() {
        return ParseMode.MARKDOWN;
    }

    default ReplyKeyboard getReply() {
        return null;
    }

    default Integer getMessageId() {
        return null;
    }

    default Integer getReplyMessageId() {
        return null;
    }

    MessageType getType();

    default String getCaption() {
        return null;
    }

    default InputFile getInputFile() {
        return null;
    }

}
