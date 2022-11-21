package uz.bot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.bot.variable.message.base.GeneralSender;

@Slf4j
@Service
public class ExecutorService {

    public SendMessage send(GeneralSender start) {
        var send = SendMessage
                .builder()
                .chatId(start.getChatId())
                .text(start.getText())
                .parseMode(start.getParseMode() == null ? ParseMode.MARKDOWN : start.getParseMode())
                .build();
        if (start.getReply() != null) send.setReplyMarkup(start.getReply());
        if (isExistReplyMessage(start)) send.setReplyToMessageId(start.getReplyMessageId());
        return send;
    }

    public EditMessageText edit(GeneralSender start) {
        var send = EditMessageText
                .builder()
                .chatId(start.getChatId())
                .text(start.getText())
                .messageId(start.getMessageId())
                .parseMode(start.getParseMode() == null ? ParseMode.MARKDOWN : start.getParseMode())
                .build();
        if (isExistKeyboard(start)) {
            InlineKeyboardMarkup reply = (InlineKeyboardMarkup) start.getReply();
            send.setReplyMarkup(reply);
        }
        return send;
    }

    public SendVoice sendVoice(GeneralSender start) {
        var send = SendVoice
                .builder()
                .chatId(start.getChatId())
                .parseMode(start.getParseMode() == null ? ParseMode.MARKDOWN : start.getParseMode())
                .voice(start.getInputFile())
                .caption(start.getCaption())
                .build();
        if (isExistKeyboard(start)) send.setReplyMarkup(start.getReply());
        if (isExistReplyMessage(start)) send.setReplyToMessageId(start.getReplyMessageId());
        return send;
    }

    private boolean isExistReplyMessage(GeneralSender start) {
        return start.getMessageId() != null;
    }

    private boolean isExistKeyboard(GeneralSender start) {
        return start.getReply() != null;
    }

}
