package serializer;

import org.springframework.lang.Nullable;

import java.util.Objects;

public class SerializedValue {
    @Nullable
    private SerializedValue next;
    @Nullable
    private final String value;

    private SerializedValue(
            @Nullable
            String value,
            @Nullable
            SerializedValue next)
    {
        this.value = value;
        this.next = next;
    }

    public static SerializedValue of(@Nullable String value) {
        return new SerializedValue(value, null);
    }

    public static SerializedValue of(
            @Nullable
            String value,
            @Nullable
            SerializedValue next)
    {
        return new SerializedValue(value, next);
    }

    public static SerializedValue empty() {
        return of(null);
    }

    public boolean setNext(SerializedValue next) {
        if (!hasNext()) {
            this.next = next;
            return true;
        }

        return false;
    }

    public String getValue() {
        return value;
    }

    @Nullable
    public SerializedValue getNext() {
        return next;
    }

    public boolean hasNext() {
        return Objects.nonNull(next);
    }

    @Override
    public String toString() {
        return "SerializedValue{" +
                "next=" + next +
                ", value='" + value + '\'' +
                '}';
    }
}
