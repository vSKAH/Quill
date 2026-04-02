package tech.skworks.hytale.quill.translation;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Quill
 * Class TranslationBuilder
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
public final class TranslationBuilder {

    private final Translatable key;
    private final String       lang;

    private Map<String, ParamValue>       params;
    private Map<String, FormattedMessage> msgParams;

    TranslationBuilder(Translatable key, String lang) {
        this.key  = key;
        this.lang = lang;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, @NotNull String value) {
        params().put(key, new StringParamValue(value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, boolean value) {
        params().put(key, new BoolParamValue(value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, int value) {
        params().put(key, new IntParamValue(value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, long value) {
        params().put(key, new LongParamValue(value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, double value) {
        params().put(key, new DoubleParamValue(value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, float value) {
        params().put(key, new DoubleParamValue((double) value));
        return this;
    }

    @NotNull public TranslationBuilder param(@NotNull String key, @NotNull Message msg) {
        msgParams().put(key, msg.getFormattedMessage());
        return this;
    }

    @NotNull
    public Message build() {
        if (params == null && msgParams == null) {
            return TranslationService.get(key, lang);
        }

        TranslationTemplate template = TranslationService.getCachedTemplate(key, lang);
        validateParams(template);
        return template.resolve(params, msgParams);
    }

    public void send(@NotNull PlayerRef player) {
        player.sendMessage(build());
    }

    public void broadcast(@NotNull Iterable<PlayerRef> players) {
        Map<String, List<PlayerRef>> byLang = new HashMap<>();
        for (PlayerRef p : players) {
            if (p.isValid()) byLang.computeIfAbsent(p.getLanguage(), k -> new ArrayList<>()).add(p);
        }

        for (var entry : byLang.entrySet()) {
            String entryLang = entry.getKey();
            Message msg;

            if (params == null && msgParams == null) {
                msg = TranslationService.get(key, entryLang);
            } else {
                TranslationTemplate template = TranslationService.getCachedTemplate(key, entryLang);
                validateParams(template);
                msg = template.resolve(params, msgParams);
            }

            for (PlayerRef p : entry.getValue()) p.sendMessage(msg);
        }
    }

    private void validateParams(TranslationTemplate template) {
        if (!template.hasParams()) return;

        List<String> missing = null;

        for (String expected : template.getParamNames()) {
            boolean provided = (params    != null && params.containsKey(expected))
                    ||         (msgParams != null && msgParams.containsKey(expected));
            if (!provided) {
                if (missing == null) missing = new ArrayList<>();
                missing.add(expected);
            }
        }

        if (missing != null) {
            throw new IllegalStateException(
                    "Translation [" + key + "] lang=[" + lang + "] missing params: " + missing
            );
        }
    }

    private Map<String, ParamValue> params() {
        if (params == null) params = new HashMap<>();
        return params;
    }

    private Map<String, FormattedMessage> msgParams() {
        if (msgParams == null) msgParams = new HashMap<>();
        return msgParams;
    }
}
