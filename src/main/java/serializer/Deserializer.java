package serializer;

import java.lang.reflect.Type;

public interface Deserializer {
    SerializedNode serialize(Type type, Object object);
    Object deserialize(Type type, SerializedNode node);
    boolean suitable(Type type);
}
