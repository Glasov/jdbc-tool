package serializer;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import serializer.primitive.PrimitiveDeserializer;

public class ReflectionUtils {
    private static final Map<Type, Deserializer> DESERIALIZERS = new ConcurrentHashMap<>();
    private static final PrimitiveDeserializer primitiveDeserializer = new PrimitiveDeserializer();

    public static Deserializer getDeserializer(Type type) {
        if (primitiveDeserializer.suitable(type)) {
            return primitiveDeserializer;
        }

        if (!DESERIALIZERS.containsKey(type)) {
            Deserializer newValue = new ObjectDeserializer((Class<?>) type);
            DESERIALIZERS.put(type, newValue);
        }

        return DESERIALIZERS.get(type);
    }

    public static int getPrimitivesCount(Type type) {
        if (type instanceof Class<?> clazz) {
            int result = 0;
            for (FieldInfo fieldInfo : ReflectionUtils.getFields(clazz)) {
                if (fieldInfo.isPrimitive()) {
                    result++;
                } else {
                    result += getPrimitivesCount(fieldInfo.getType());
                }
            }
            return result;
        } else {
            return 0;
        }
    }

    public static List<FieldInfo> getFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(f -> {
                    Type fieldType = f.getType();
                    boolean isOptional = isOptional(fieldType);
                    if (isOptional) fieldType = getOptionalGenericTypeOrSelf(f.getGenericType());
                    return new FieldInfo(f, fieldType, isOptional, primitiveDeserializer.suitable(fieldType), getDeserializer(fieldType));
                })
                .collect(Collectors.toList());
    }

    private static Type getOptionalGenericTypeOrSelf(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private static boolean isOptional(Type type) {
        return type.equals(Optional.class);
    }
}
