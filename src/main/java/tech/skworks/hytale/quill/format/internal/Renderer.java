package tech.skworks.hytale.quill.format.internal;

import com.hypixel.hytale.server.core.Message;

import java.util.*;

/**
 * Project Quill
 * Class Renderer
 *
 * @author Jimmy (vSKAH) - 01/04/2026
 * @version 1.0
 * @since 1.0.0-SNAPSHOT
 */
public class Renderer {

    private final String tag;
    private final List<Object> children = new ArrayList<>();

    Renderer(String tag) {
        this.tag = tag;
    }

    Message render(StyleState parentStyle, GradientContext gradCtx) {
        StyleState myStyle = new StyleState(parentStyle);
        StyleState.applyTagToStyle(tag, myStyle);

        if (myStyle.gradient != null && gradCtx == null) {
            gradCtx = new GradientContext(myStyle.gradient.colors(), calculateTextLength());
        }

        List<Message> messages = new ArrayList<>(children.size());

        for (Object child : children) {
            if (child instanceof String text) {
                if (gradCtx != null) {
                    for (int i = 0; i < text.length(); i++) {
                        Message msg = Message.raw(String.valueOf(text.charAt(i)));
                        StyleState.applyStyle(msg, myStyle);
                        msg.color(gradCtx.next());
                        messages.add(msg);
                    }
                } else {
                    Message msg = Message.raw(text);
                    StyleState.applyStyle(msg, myStyle);
                    messages.add(msg);
                }
            } else if (child instanceof Renderer node) {
                messages.add(node.render(myStyle, gradCtx));
            }
        }

        return messages.isEmpty() ? Message.raw("") : Message.join(messages.toArray(new Message[0]));
    }

    int calculateTextLength() {
        int len = 0;
        for (Object child : children) {
            if (child instanceof String s) len += s.length();
            else if (child instanceof Renderer n) len += n.calculateTextLength();
        }
        return len;
    }


    public static Message render(List<Token> tokens) {
        Renderer root = new Renderer("root");
        buildTree(tokens, root);
        return root.render(new StyleState(), null);
    }

    static void buildTree(List<Token> tokens, Renderer root) {
        Deque<Renderer> stack = new ArrayDeque<>();
        stack.push(root);

        for (Token token : tokens) {
            switch (token) {
                case Token.Text t -> Objects.requireNonNull(stack.peek()).children.add(t.content());
                case Token.OpenTag t -> {
                    Renderer node = new Renderer(t.name());
                    Objects.requireNonNull(stack.peek()).children.add(node);
                    stack.push(node);
                }
                case Token.CloseTag _ -> {
                    if (stack.size() > 1) stack.pop();
                }
            }
        }

    }
}
