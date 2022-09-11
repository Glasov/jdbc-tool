package serializer;

import java.util.Iterator;
import java.util.Objects;

public class SerializedNode implements Iterator<String> {
    private SerializedValue current;
    private SerializedValue head;
    private SerializedValue tail;

    public SerializedNode(SerializedValue current) {
        this.current = current;
        this.head = current;
        this.tail = calculateTail();
    }

    public static SerializedNode empty() {
        return new SerializedNode(null);
    }

    public static SerializedNode empty(int emptyNodes) {
        SerializedValue head = SerializedValue.empty();
        SerializedValue now = head;

        for (int i = 1; i < emptyNodes; i++) {
            now.setNext(SerializedValue.empty());
            now = now.getNext();
        }

        return new SerializedNode(head);
    }

    public static SerializedNode of(SerializedValue serializedValue) {
        return new SerializedNode(serializedValue);
    }

    public static SerializedNode of(String serializedValue) {
        return new SerializedNode(SerializedValue.of(serializedValue));
    }

    public static SerializedNode of(String serializedValue, SerializedValue next) {
        return new SerializedNode(SerializedValue.of(serializedValue, next));
    }

    public boolean isNullValue() {
        return Objects.isNull(current.getValue());
    }

    @Override
    public boolean hasNext() {
        return current.hasNext();
    }

    @Override
    public String next() {
        String value = current.getValue();
        current = current.getNext();
        return value;
    }

    public SerializedValue append(SerializedNode other) {
        if (Objects.isNull(head)) {
            this.head = other.head;
            this.current = other.current;
            this.tail = calculateTail();
        } else if (Objects.isNull(other.head)) {
            return current;
        } else if (Objects.isNull(current)) {
            this.head = other.head;
            this.current = other.current;
            this.tail = other.tail;
        } else {
            this.tail.setNext(other.head);
            this.tail = calculateTail();
        }

        return current;
    }

    public SerializedValue getHead() {
        return head;
    }

    public SerializedValue getTail() {
        return tail;
    }

    public SerializedValue calculateTail() {
        SerializedValue value = current;
        while (Objects.nonNull(value) && value.hasNext()) {
            value = value.getNext();
        }
        return value;
    }

    public String join(String separator) {
        StringBuilder result = new StringBuilder("(");
        SerializedValue tmp = head;
        while (Objects.nonNull(tmp)) {
            result.append(tmp.getValue());
            if (tmp.hasNext()) result.append(separator);
            tmp = tmp.getNext();
        }

        result.append(")");

        return result.toString();
    }

    public String join() {
        return join(",");
    }

    @Override
    public String toString() {
        return "SerializedNode{" +
                "current=" + current +
                ", head=" + head +
                ", tail=" + tail +
                '}';
    }
}
