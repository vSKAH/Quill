package tech.skworks.hytale.quill.format.internal;

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project Quill
 * Class StyleState
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
class StyleState {

    String color = null;
    boolean bold = false;
    boolean italic = false;
    boolean underlined = false;
    boolean monospace = false;
    GradientSpec gradient = null;

    StyleState() {
    }

    StyleState(StyleState other) {
        this.color = other.color;
        this.bold = other.bold;
        this.italic = other.italic;
        this.underlined = other.underlined;
        this.monospace = other.monospace;
        this.gradient = other.gradient;
    }

    static void applyTagToStyle(String tag, StyleState style) {
        if ("root".equals(tag)) return;

        int colonIdx = tag.indexOf(':');
        String name = (colonIdx == -1 ? tag : tag.substring(0, colonIdx)).toLowerCase();
        String rest = colonIdx == -1 ? "" : tag.substring(colonIdx + 1);

        switch (name) {
            case "bold", "b" -> style.bold = true;
            case "italic", "i", "em" -> style.italic = true;
            case "underlined", "u" -> style.underlined = true;
            case "monospace", "mono", "tt" -> style.monospace = true;
            case "gradient" -> applyGradient(rest, style);
            case "color" -> style.color = ColorResolver.resolveColor(rest);
            default -> {
                String resolved = ColorResolver.resolveColor(tag);
                if (resolved != null) style.color = resolved;
            }
        }
    }

    static void applyGradient(String colorPart, StyleState style) {
        String[] parts = colorPart.split(":", -1);
        if (parts.length < 2) return;

        List<String> stops = new ArrayList<>(parts.length);
        for (String part : parts) {
            String c = ColorResolver.resolveColor(part);
            stops.add(c != null ? c : "#ffffff");
        }
        style.gradient = new GradientSpec(Collections.unmodifiableList(stops));
        style.color = null;
    }

    static void applyStyle(Message msg, StyleState style) {
        if (style.color != null && style.gradient == null) msg.color(style.color);
        if (style.bold) msg.bold(true);
        if (style.italic) msg.italic(true);
        if (style.underlined) msg.getFormattedMessage().underlined = MaybeBool.True;
        if (style.monospace) msg.monospace(true);
    }


}
