package tech.skworks.hytale.quill.format.internal;

import java.util.List;

/**
 * Project Quill
 * Class GradientContext
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
class GradientContext {

    private final String[] palette;
    private int pos = 0;

    GradientContext(List<String> colorStops, int total) {
        palette = new String[Math.max(total, 1)];
        for (int i = 0; i < total; i++) {
            palette[i] = ColorResolver.computeGradientColor(colorStops, i, total);
        }
    }

    String next() {
        return pos < palette.length ? palette[pos++] : palette[palette.length - 1];
    }

}
