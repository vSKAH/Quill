package tech.skworks.hytale.quill.format;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Quill
 * Class Colors
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
@Getter
public enum Colors {

    BLACK("#000000"),
    DARK_BLUE("#0000AA"),
    DARK_GREEN("#00AA00"),
    DARK_AQUA("#00AAAA"),
    DARK_RED("#AA0000"),
    DARK_PURPLE("#AA00AA"),
    GOLD("#FFAA00"),
    GRAY("#AAAAAA"),
    DARK_GRAY("#555555"),
    BLUE("#5555FF"),
    GREEN("#55FF55"),
    AQUA("#55FFFF"),
    RED("#FF5555"),
    LIGHT_PURPLE("#FF55FF"),
    YELLOW("#FFFF55"),
    WHITE("#FFFFFF"),

    ORANGE("#FF8000"),
    PINK("#FF69B4"),
    LIME("#32CD32"),
    CYAN("#00FFFF"),
    MAGENTA("#FF00FF"),
    TEAL("#008080"),
    NAVY("#000080"),
    MAROON("#800000"),
    OLIVE("#808000"),
    SILVER("#C0C0C0"),
    CORAL("#FF7F50"),
    SALMON("#FA8072"),
    LAVENDER("#E6E6FA"),
    TURQUOISE("#40E0D0"),
    INDIGO("#4B0082"),
    VIOLET("#EE82EE");

    private final String hex;

    Colors(String hex) {
        this.hex = hex;
    }

    private static final Map<String, String> BY_NAME;

    static {
        Map<String, String> m = new HashMap<>(values().length * 2);
        for (Colors c : values()) m.put(c.name().toLowerCase(), c.hex);
        BY_NAME = Collections.unmodifiableMap(m);
    }

    public static String resolve(String name) {
        if (name == null || name.isEmpty()) return null;
        return BY_NAME.get(name.toLowerCase());
    }

    @Override
    public String toString() {
        return hex;
    }
}
