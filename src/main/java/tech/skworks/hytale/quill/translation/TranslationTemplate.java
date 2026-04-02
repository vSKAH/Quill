package tech.skworks.hytale.quill.translation;

import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.util.MessageUtil;
import tech.skworks.hytale.quill.Quill;
import tech.skworks.hytale.quill.format.ParsedMessage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Project Quill
 * Class TranslationTemplate
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
public class TranslationTemplate {

    private final String rawString;
    private final ParsedMessage parsedTemplate;
    private volatile Message cachedResolvedMessage;

    private TranslationTemplate(String rawString) {
        this.rawString = rawString;
        this.parsedTemplate = Quill.parse(rawString);
    }

    public static TranslationTemplate from(String raw) {
        return new TranslationTemplate(raw);
    }

    public boolean hasParams() {
        return parsedTemplate.hasParams();
    }

    public boolean hasColors() {
        return parsedTemplate.hasColors();
    }

    public List<String> getParamNames() {
        return parsedTemplate.getParamNames();
    }

    public Message resolve(@Nullable Map<String, ParamValue> params,
                           @Nullable Map<String, FormattedMessage> msgParams) {
        boolean noParams = (params == null || params.isEmpty())
                && (msgParams == null || msgParams.isEmpty());

        if (noParams) {
            Message cached = cachedResolvedMessage;
            if (cached != null) return cached;

            synchronized (this) {
                if (cachedResolvedMessage == null) {
                    cachedResolvedMessage = parsedTemplate.getResult();
                }
                return cachedResolvedMessage;
            }
        }

        String substituted = MessageUtil.formatText(rawString, params, msgParams);
        return Quill.parse(substituted).getResult();
    }
}
