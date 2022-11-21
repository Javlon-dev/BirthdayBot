package uz.bot.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.bot.service.base.AbstractService;
import uz.bot.variable.message.base.GeneralSender;

@Service
@Slf4j
public class CallBackQueryService extends AbstractService {

    public GeneralSender start(Long chatId, CallbackQuery callbackQuery) {
        return startMenu(chatId, callbackQuery.getData());
    }


}
