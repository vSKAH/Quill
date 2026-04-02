package fr.sktechnology.hytale.quill.translation;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.MessageUtil;
import fr.sktechnology.hytale.quill.Quill;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Project Quill
 * Class TranslationService
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
@UtilityClass
public class TranslationService {

    private static final ConcurrentHashMap<String, ConcurrentHashMap<Translatable, TranslationTemplate>> TEMPLATE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ConcurrentHashMap<Translatable, Message>> MESSAGE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Translatable> REGISTRY = new ConcurrentHashMap<>();
    public static final String DEFAULT_LANG = "en-US";

    public static void register(Translatable... values) {
        for (Translatable t : values) REGISTRY.put(t.getMessageId(), t);
    }

    public static TranslationTemplate getCachedTemplate(Translatable key, String lang) {
        ConcurrentHashMap<Translatable, TranslationTemplate> langMap =
                TEMPLATE_CACHE.computeIfAbsent(lang, k -> new ConcurrentHashMap<>());
        return langMap.computeIfAbsent(key, k -> loadTemplate(key, lang));
    }

    private static TranslationTemplate loadTemplate(Translatable key, String lang) {
        String raw = I18nModule.get().getMessage(lang, key.getMessageId());
        return TranslationTemplate.from(raw != null ? raw : key.getMessageId());
    }

    public static Message getCachedMessage(Translatable key, String lang) {
        TranslationTemplate template = getCachedTemplate(key, lang);

        if (template.hasParams()) {
            return template.resolve(null, null);
        }

        ConcurrentHashMap<Translatable, Message> langMap =
                MESSAGE_CACHE.computeIfAbsent(lang, k -> new ConcurrentHashMap<>());

        return langMap.computeIfAbsent(key, k -> template.resolve(null, null));
    }

    public static Message get(@NotNull Translatable key, @NotNull String lang) {
        return getCachedMessage(key, lang);
    }

    public static Message get(@NotNull Translatable key, @Nullable PlayerRef playerRef) {
        return getCachedMessage(key, lang(playerRef));
    }

    // Pas did -> parse le rawText directement via SKFormat
    // Id inconnu dans l'enum -> lookup i18n + SKFOrmat
    // Id connu sans params -> Message caché
    // Id connu avec params -> résolution via le template compilé
    public static Message resolve(@NotNull Message msgWithParams, @Nullable PlayerRef playerRef) {
        String id = msgWithParams.getMessageId();
        String lang = lang(playerRef);
        var fmtMsg = msgWithParams.getFormattedMessage();
        boolean hasParams = fmtMsg != null && (fmtMsg.params != null || fmtMsg.messageParams != null);

        if (id == null) {
            String raw = msgWithParams.getRawText() != null ? msgWithParams.getRawText() : "";
            if (hasParams) raw = MessageUtil.formatText(raw, fmtMsg.params, fmtMsg.messageParams);
            return Quill.parse(raw).getResult();
        }

        Translatable key = REGISTRY.get(id);
        if (key == null) {
            String raw = I18nModule.get().getMessage(lang, id);
            if (raw == null) raw = id;
            if (hasParams) raw = MessageUtil.formatText(raw, fmtMsg.params, fmtMsg.messageParams);
            return Quill.parse(raw).getResult();
        }

        if (!hasParams) return getCachedMessage(key, lang);
        return getCachedTemplate(key, lang).resolve(fmtMsg.params, fmtMsg.messageParams);
    }

    public static TranslationBuilder builder(@NotNull Translatable key, @Nullable PlayerRef playerRef) {
        return new TranslationBuilder(key, lang(playerRef));
    }

    public static TranslationBuilder builder(@NotNull Translatable key, @NotNull String lang) {
        return new TranslationBuilder(key, lang);
    }

    public static void invalidateAll() {
        TEMPLATE_CACHE.clear();
        MESSAGE_CACHE.clear();
    }

    public static void invalidate(@NotNull String lang) {
        TEMPLATE_CACHE.remove(lang);
        MESSAGE_CACHE.remove(lang);
    }

    public static String lang(@Nullable PlayerRef ref) {
        return (ref == null || !ref.isValid()) ? DEFAULT_LANG : ref.getLanguage();
    }

}
