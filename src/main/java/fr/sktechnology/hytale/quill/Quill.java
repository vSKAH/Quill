package fr.sktechnology.hytale.quill;

import com.hypixel.hytale.server.core.Message;
import fr.sktechnology.hytale.quill.format.ParsedMessage;
import fr.sktechnology.hytale.quill.format.internal.Renderer;
import fr.sktechnology.hytale.quill.format.internal.Token;
import fr.sktechnology.hytale.quill.format.internal.Tokenizer;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Project Quill
 * Class Quill
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */

@UtilityClass
public class Quill {

    public static ParsedMessage parse(@NotNull String input) {

        if (!input.contains("<")) {
            return new ParsedMessage(input, Message.raw(input), false, 0);
        }

        List<Token> tokens = Tokenizer.tokenize(input);

        int tagCount = 0;
        for (Token t : tokens) {
            if (!(t instanceof Token.Text)) tagCount++;
        }

        Message result = Renderer.render(tokens);
        return new ParsedMessage(input, result, tagCount > 0, tagCount);
    }

}
