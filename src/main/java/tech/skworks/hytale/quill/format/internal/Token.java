package tech.skworks.hytale.quill.format.internal;

/**
 * Project Quill
 * Class Token
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
public sealed interface Token permits Token.Text, Token.OpenTag, Token.CloseTag {
    record Text(String content) implements Token {
    }

    record OpenTag(String name) implements Token {
    }

    record CloseTag(String name) implements Token {
    }
}
