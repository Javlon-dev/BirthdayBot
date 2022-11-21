package uz.bot.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
public class Utils {

    public static DateTimeFormatter formatDDMMYYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String toFormatDDMMYYYY(LocalDate date) {
        return date.format(formatDDMMYYYY);
    }

    public static LocalDate toFormatDDMMYYYY(String date) {
        return LocalDate.parse(date, formatDDMMYYYY);
    }

    public static Pattern patternForName = Pattern.compile("^[A-Z][a-z]{2,20}$");

    public static Pattern patternForDate = Pattern.compile("^([0-2][0-9]||3[0-1]).(0[0-9]||1[0-2]).(19[4-9][0-9]||20[0-5][0-9])$");

    public static boolean checkPatternName(String name) {
        return !patternForName.matcher(name).matches();
    }

    public static boolean checkPatternDate(String date) {
        return !patternForDate.matcher(date).matches();
    }

    public static boolean checkDate(String date) {
        LocalDate localDate = toFormatDDMMYYYY(date);
        return localDate.getYear() > 2015;
    }

    public static boolean checkIsNumber(String text) {
        try {
            Long.valueOf(text);
            return true;
        } catch (Exception e) {
            log.warn("checkIsNumber : " + e);
            return false;
        }
    }

    public static boolean checkForNull(Object... values) {
        if (values == null) return true;
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

}
