package serializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.objenesis.ObjenesisStd;

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
        if (Objects.isNull(object)) {
            return SerializedNode.empty(ReflectionUtils.getPrimitivesCount(type));
        }
        SerializedNode node = SerializedNode.empty();
        for (FieldInfo field : fields) {
            try {
                Object value = field.get(object);
                if (value instanceof Optional<?> optional) {
                    value = optional.orElse(null);
                }
                node.append(field.getDeserializer().serialize(field.getType(), value));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return SerializedNode.of(node.getHead());
    }

    @Override
    public Object deserialize(Type type, SerializedNode node) {
        Object instance = INSTANCE_SUPPLIER.newInstance(clazz);
        int setFields = 0;

        for (FieldInfo field : fields) {
            Type fieldType = field.getType();
            Deserializer deserializer = field.getDeserializer();
            Object fieldValue = deserializer.deserialize(fieldType, node);
            setFields += (Objects.nonNull(fieldValue) || field.isOptional()) ? 1 : 0;
            try {
                if (field.isOptional()) fieldValue = Optional.ofNullable(fieldValue);
                if (Objects.nonNull(fieldValue)) field.setRaw(instance, fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return setFields == fields.size() ? instance : null;
    }

    @Override
    public boolean suitable(Type type) {
        return type.equals(clazz);
    }
}
