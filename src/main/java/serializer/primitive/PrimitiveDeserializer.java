package serializer.primitive;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import serializer.Deserializer;
import serializer.SerializedNode;

import java.lang.reflect.Type;
import java.util.List;

public class PrimitiveDeserializer implements Deserializer {
    private static final List<Deserializer> DESERIALIZERS = List.of(
            new BooleanDeserializer(),
            new ByteDeserializer(),
            new CharDeserializer(),
            new DoubleDeserializer(),
            new FloatDeserializer(),
            new IntegerDeserializer(),
            new LongDeserializer(),
            new ShortDeserializer(),
            new StringDeserializer()
    );

    private static Deserializer getSuitableOrThrow(Type type) {
        return DESERIALIZERS.stream().filter(d -> d.suitable(type)).findFirst().orElseThrow();
    }

    @Override
    public SerializedNode serialize(Type type, @Nullable Object object) {
        Deserializer deserializer = getSuitableOrThrow(type);
        return deserializer.serialize(type, object);
    }

    @Override
    public Object deserialize(Type type, @NonNull SerializedNode node) {
        if (node.isNullValue()) {
            node.next();
            return null;
        }
        return getSuitableOrThrow(type).deserialize(type, node);
    }

    @Override
    public boolean suitable(Type type) {
        return DESERIALIZERS.stream().anyMatch(d -> d.suitable(type));
    }
}
