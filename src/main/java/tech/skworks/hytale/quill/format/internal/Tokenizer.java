package tech.skworks.hytale.quill.format.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Quill
 * Class Tokenizer
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
public class Tokenizer {

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>(input.length() / 8 + 4);
        final int len = input.length();
        int textStart = 0;
        int i = 0;

        while (i < len) {
            int open = input.indexOf('<', i);
            if (open == -1) break;

            int close = input.indexOf('>', open + 1);
            if (close == -1) break;

            int nested = input.indexOf('<', open + 1);
            if (nested != -1 && nested < close) {
                i = open + 1;
                continue;
            }

            if (open > textStart) {
                tokens.add(new Token.Text(input.substring(textStart, open)));
            }

            String content = input.substring(open + 1, close);
            if (!content.isEmpty() && content.charAt(0) == '/') {
                tokens.add(new Token.CloseTag(content.substring(1)));
            } else {
                tokens.add(new Token.OpenTag(content));
            }

            i = close + 1;
            textStart = i;
        }

        if (textStart < len) {
            tokens.add(new Token.Text(input.substring(textStart)));
        }

        return tokens;
    }

}
