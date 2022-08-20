package serializer;

import org.springframework.lang.Nullable;

import java.util.Iterator;
import java.util.Objects;

public class SerializedNode implements Iterator<SerializedNode> {
    @Nullable
    private SerializedNode next = null;
    @Nullable
    private final String value;

    public SerializedNode(@Nullable String value) {
        this.value = value;
    }

    public SerializedNode(
            @Nullable
            String value,
            @Nullable
            SerializedNode next)
    {
        this.value = value;
        this.next = next;
    }

    public static SerializedNode of(@Nullable String value) {
        return new SerializedNode(value);
    }

    public static SerializedNode of(
            @Nullable
            String value,
            @Nullable
            SerializedNode next)
    {
        return new SerializedNode(value, next);
    }

    public static SerializedNode empty() {
        return of(null);
    }

    /**
     * returns boolean to prevent memory leaks.
     * o -> o -> null
     * if applied to first element without a check, the second element causes a memory leak
     */
    public boolean setNext(SerializedNode next) {
        if (Objects.nonNull(next)) {
            this.next = next;
            return true;
        }

        return false;
    }

    public String value() {
        return value;
    }

    public SerializedNode getTail() {
        SerializedNode node = this;

        while (node.hasNext()) {
            node = node.next();
        }

        return node;
    }

    @Override
    public boolean hasNext() {
        return Objects.nonNull(next);
    }

    @Override
    public SerializedNode next() {
        return next;
    }

    @Override
    public String toString() {
        return "DeserializedNode{" +
                "next=" + next +
                ", value=" + (Objects.nonNull(value) ? "'" + value + "'" : "null") +
                '}';
    }
}
