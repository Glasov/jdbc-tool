package serializer.primitive;

import serializer.SerializedNode;
import serializer.Deserializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class CharDeserializer implements Deserializer {
    @Override
    public SerializedNode serialize(Type type, Object object) {
        if (Objects.isNull(object)) {
            return SerializedNode.empty();
        }
        return SerializedNode.of(object.toString());
    }

    @Override
    public Object deserialize(Type type, SerializedNode node) {
        return node.value().charAt(0);
    }

    @Override
    public boolean suitable(Type type) {
        return type.equals(char.class) || type.equals(Character.class);
    }
}
