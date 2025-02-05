package pl.dawid0604.mplayer.tools;

import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.Clock.systemDefaultZone;

public final class DateFormatter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    private DateFormatter() { }

    public static String withDateFormat(@NonNull final LocalDate date) {
        return DATE_FORMAT.format(date);
    }

    public static String withDateFormat(@NonNull final LocalDateTime date) {
        return DATE_FORMAT.format(date);
    }

    public static LocalDateTime getCurrentDate() {
        return LocalDateTime.now(systemDefaultZone());
    }
}
