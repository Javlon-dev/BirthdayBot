package uz.bot.variable.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.bot.variable.enums.MessageType;
import uz.bot.variable.message.base.GeneralSender;

@Getter
@Setter
@Builder
@ToString
public class EditorMessage implements GeneralSender {
    private Long chatId;
    private String text;
    private String parseMode;
    private ReplyKeyboard reply;
    private Integer messageId;

    @Override
    public MessageType getType() {
        return MessageType.EDIT_MESSAGE;
    }
}
