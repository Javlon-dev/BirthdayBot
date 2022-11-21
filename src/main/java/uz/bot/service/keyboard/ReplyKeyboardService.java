package uz.bot.service.keyboard;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReplyKeyboardService {

    public KeyboardRow getRows(KeyboardButton... rows) {
        KeyboardRow row = new KeyboardRow();
        row.addAll(Arrays.asList(rows));
        return row;
    }

    public KeyboardButton getButton(String text) {
        return KeyboardButton
                .builder()
                .text(text)
                .build();
    }

    public KeyboardButton getRequestContactButton(String text) {
        return KeyboardButton
                .builder()
                .text(text)
                .requestContact(true)
                .build();
    }

    public KeyboardButton getRequestLocationButton(String text) {
        return KeyboardButton
                .builder()
                .text(text)
                .requestLocation(true)
                .build();
    }

    public ReplyKeyboardMarkup getKeyboard(KeyboardRow... rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(new ArrayList<>(List.of(rows)));
        return replyKeyboardMarkup;
    }

}
