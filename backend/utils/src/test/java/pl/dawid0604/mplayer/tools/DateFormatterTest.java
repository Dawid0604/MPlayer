package pl.dawid0604.mplayer.tools;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateFormatterTest {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{2}.\\d{2}.\\d{4}");

    @Test
    void shouldFormatWithDateFormat() {
        // Given
        LocalDate date = LocalDate.now();

        // When
        String formattedDate = DateFormatter.withDateFormat(date);

        // Then
        assertTrue(DATE_PATTERN.matcher(formattedDate).matches());
    }

    @Test
    void shouldFormatWithDateFormat2() {
        // Given
        LocalDateTime date = LocalDateTime.now();

        // When
        String formattedDate = DateFormatter.withDateFormat(date);

        // Then
        assertTrue(DATE_PATTERN.matcher(formattedDate).matches());
    }

    @Test
    void shouldGetCurrentDate() {
        // Given
        // When
        // Then
        assertNotNull(DateFormatter.getCurrentDate());
    }
}