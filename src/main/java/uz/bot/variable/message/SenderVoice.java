package uz.bot.variable.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.bot.variable.enums.MessageType;
import uz.bot.variable.message.base.GeneralSender;

@Getter
@Setter
@Builder
@ToString
public class SenderVoice implements GeneralSender {
    private Long chatId;
    private String parseMode;
    private ReplyKeyboard reply;
    private Integer replyMessageId;
    private String caption;
    private InputFile inputFile;

    @Override
    public MessageType getType() {
        return MessageType.VOICE_MESSAGE;
    }

}
