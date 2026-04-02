package fr.sktechnology.hytale.quill.format.internal;

import fr.sktechnology.hytale.quill.format.Colors;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project Quill
 * Class ColorResolver
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
class ColorResolver {

    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final ConcurrentHashMap<String, Color> AWT_COLOR_CACHE = new ConcurrentHashMap<>(64);

    static Color cachedDecode(String hex) {
        return AWT_COLOR_CACHE.computeIfAbsent(hex, h -> {
            try {
                return Color.decode(h);
            } catch (NumberFormatException e) {
                return Color.WHITE;
            }
        });
    }

    static String resolveColor(String input) {
        if (input == null || input.isEmpty()) return null;
        if (input.charAt(0) == '#' && input.length() == 7) return input;
        return Colors.resolve(input);
    }

    static String hexColor(int r, int g, int b) {
        char[] buf = {
                '#',
                HEX[(r >> 4) & 0xF], HEX[r & 0xF],
                HEX[(g >> 4) & 0xF], HEX[g & 0xF],
                HEX[(b >> 4) & 0xF], HEX[b & 0xF]
        };
        return new String(buf);
    }

    static String computeGradientColor(List<String> colors, int index, int total) {
        if (colors.isEmpty()) return "#ffffff";
        if (colors.size() == 1 || total <= 1) return colors.getFirst();

        float segmentSize = (float) total / (colors.size() - 1);
        int segment = Math.min((int) (index / segmentSize), colors.size() - 2);
        float fraction = (index % segmentSize) / segmentSize;

        Color c1 = cachedDecode(colors.get(segment));
        Color c2 = cachedDecode(colors.get(segment + 1));

        int r = Math.round(c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
        int g = Math.round(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
        int b = Math.round(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);

        return hexColor(r, g, b);
    }
}
