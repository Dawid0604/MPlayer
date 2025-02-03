package pl.dawid0604.mplayer.tools;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class RegexTool {
    private RegexTool() { }
    public static final Pattern COMMA_PATTERN = patternFrom(",");
    public static final Pattern COLON_PATTERN = patternFrom(":");
    public static final Pattern SPACE_PATTERN = patternFrom("\\s+");

    public static List<String> split(final String text, final Pattern pattern) {
        if(isBlank(text)) {
            return emptyList();
        }

        return Arrays.stream(pattern.split(text))
                     .toList();
    }

    public static Pattern patternFrom(final String regex) {
        return Pattern.compile(regex, CASE_INSENSITIVE | UNICODE_CASE);
    }
}
