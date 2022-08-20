package serializer;

import org.springframework.objenesis.ObjenesisStd;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObjectDeserializer implements Deserializer {
    private static final ObjenesisStd INSTANCE_SUPPLIER = new ObjenesisStd(true);

    private final List<FieldInfo> fields;
    private final Class<?> clazz;

    public ObjectDeserializer(Class<?> clazz) {
        this.clazz = clazz;
        this.fields = ReflectionUtils.getFields(clazz);
    }

    @Override
    public SerializedNode serialize(Type type, Object object) {
        SerializedNode head = SerializedNode.empty();
        SerializedNode tail = head;
        for (FieldInfo field : fields) {
            try {
                Object value = field.get(object);
                if (value instanceof Optional<?> optional) {
                    if (field.isPrimitive()) {
                        value = optional.orElse(null);
                    } else {
                        value = optional.isEmpty() ? INSTANCE_SUPPLIER.newInstance((Class<?>) field.getType()) : optional.get();
                    }
                }
                tail.setNext(field.getDeserializer().serialize(field.getType(), value));
                tail = tail.getTail();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return head.next();
    }

    @Override
    public Object deserialize(Type type, SerializedNode node) {
        Object instance = INSTANCE_SUPPLIER.newInstance(clazz);
        boolean setAnyField = false;

        for (FieldInfo field : fields) {
            Type fieldType = field.getType();
            Deserializer deserializer = field.getDeserializer();
            Object fieldValue = deserializer.deserialize(fieldType, node);
            setAnyField = setAnyField || Objects.nonNull(fieldValue);
            try {
                field.set(instance, field.isOptional() ? Optional.ofNullable(fieldValue) : fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return setAnyField ? instance : null;
    }

    @Override
    public boolean suitable(Type type) {
        return type.equals(clazz);
    }
}
