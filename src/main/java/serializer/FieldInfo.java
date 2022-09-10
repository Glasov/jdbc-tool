package serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class FieldInfo {
    private final Field field;
    private final Type type;
    private final boolean isOptional;
    private final Set<Class<? extends Annotation>> annotations;
    private final boolean isPrimitive;
    private final Deserializer deserializer;

    public FieldInfo(Field field, Type type, boolean isOptional, boolean isPrimitive, Deserializer deserializer) {
        this.field = field;
        this.type = type;
        this.isOptional = isOptional;
        this.annotations = Stream.of(field.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
        this.isPrimitive = isPrimitive;
        this.deserializer = deserializer;
        this.field.setAccessible(true);
    }

    public Type getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public String getName() {
        return field.getName();
    }

    public Set<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return annotations.contains(annotation);
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }

    public void setRaw(
            @NonNull
            Object object,
            @Nullable
            Object value) throws IllegalAccessException
    {
        field.set(object, isOptional ? Optional.ofNullable(value) : value);
    }

    public Object get(@NonNull Object object) throws IllegalAccessException {
        return field.get(object);
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "field=" + field +
                ", type=" + type +
                ", isOptional=" + isOptional +
                ", annotations=" + annotations +
                ", isPrimitive=" + isPrimitive +
                '}';
    }
}
