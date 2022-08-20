package serializer.primitive;

import serializer.SerializedNode;
import serializer.Deserializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private static Optional<Deserializer> getSuitable(Type type) {
        return DESERIALIZERS.stream().filter(d -> d.suitable(type)).findFirst();
    }

    @Override
    public SerializedNode serialize(Type type, Object object) {
        Optional<Deserializer> deserializer = getSuitable(type);
        if (deserializer.isEmpty()) {
            throw new RuntimeException("not found primitive deserializer for " + type);
        }
        return deserializer.get().serialize(type, object);
    }

    @Override
    public Object deserialize(Type type, SerializedNode node) {
        if (Objects.isNull(node.value())) {
            return null;
        }
        Optional<Deserializer> deserializer = getSuitable(type);
        if (deserializer.isEmpty()) {
            throw new RuntimeException("not found primitive deserializer for " + type);
        }
        return deserializer.get().deserialize(type, node);
    }

    @Override
    public boolean suitable(Type type) {
        return DESERIALIZERS.stream().anyMatch(d -> d.suitable(type));
    }
}
