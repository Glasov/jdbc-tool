package serializer.primitive;

import serializer.Deserializer;
import serializer.SerializedNode;

import java.lang.reflect.Type;
import java.util.Objects;

public class FloatDeserializer implements Deserializer {
    @Override
    public SerializedNode serialize(Type type, Object object) {
        if (Objects.isNull(object)) {
            return SerializedNode.empty();
        }
        return SerializedNode.of(object.toString());
    }

    @Override
    public Object deserialize(Type type, SerializedNode node) {
        return Float.parseFloat(node.next());
    }

    @Override
    public boolean suitable(Type type) {
        return type.equals(float.class) || type.equals(Float.class);
    }
}
