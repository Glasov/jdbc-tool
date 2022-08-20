package serializer;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FieldInfo {
    private final Field field;
    private final Type type;
    private final boolean isOptional;
    private final Set<Annotation> annotations;
    private final boolean isPrimitive;
    private final Deserializer deserializer;

    public FieldInfo(Field field, Type type, boolean isOptional, boolean isPrimitive, Deserializer deserializer) {
        this.field = field;
        this.type = type;
        this.isOptional = isOptional;
        this.annotations = Set.of(field.getDeclaredAnnotations());
        this.isPrimitive = isPrimitive;
        this.deserializer = deserializer;
        field.setAccessible(true);
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

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public boolean hasAnnotation(Annotation annotation) {
        return annotations.contains(annotation);
    }

    public Deserializer getDeserializer() {
        return deserializer;
    }

    public void set(
            @NonNull
            Object object,
            @Nullable
            Object value) throws IllegalAccessException
    {
        if (Objects.nonNull(value) && !type.equals(value.getClass())) {
            throw new IllegalArgumentException(
                    "can't set object field. wrong type: expected " + type.getTypeName() +
                            ", got " + value.getClass().getTypeName()
            );
        }
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
