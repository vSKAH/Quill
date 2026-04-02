package tech.skworks.hytale.quill.format;

import com.hypixel.hytale.server.core.Message;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project Quill
 * Class ParsedMessage
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
@Getter
@ToString(exclude = "result")
@EqualsAndHashCode(of = {"rawInput", "hasColors", "colorsCount", "hasParams"})
public class ParsedMessage {

    private static final Pattern STRIP_TAGS = Pattern.compile("</?[^<>]*>");
    private static final Pattern PARAM_REGEX = Pattern.compile("\\{([^{}]+)}");

    private final String rawInput;
    private final Message result;

    @Getter(AccessLevel.NONE)
    private final boolean hasColors;
    private final int colorsCount;

    @Getter(AccessLevel.NONE)
    private final boolean hasParams;
    private final List<String> paramNames;

    public ParsedMessage(@NotNull final String rawInput, @NotNull final Message result, final boolean hasColors, final int colorsCount) {
        this.rawInput = rawInput;
        this.result = result;
        this.hasColors = hasColors;
        this.colorsCount = colorsCount;

        if (rawInput.indexOf('{') == -1) {
            this.hasParams = false;
            this.paramNames = Collections.emptyList();
        } else {
            final Matcher matcher = PARAM_REGEX.matcher(rawInput);
            final SequencedSet<String> names = new LinkedHashSet<>();
            while (matcher.find()) names.add(matcher.group(1));
            this.hasParams = !names.isEmpty();
            this.paramNames = List.copyOf(names);
        }
    }

    public boolean hasColors() {
        return hasColors;
    }

    public boolean hasParams() {
        return hasParams;
    }

    public String getTextWithoutColors() {
        return STRIP_TAGS.matcher(rawInput).replaceAll("");
    }
}
