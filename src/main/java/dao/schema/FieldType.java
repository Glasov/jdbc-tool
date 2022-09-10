package dao.schema;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public enum FieldType {
    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,
    STRING,
    NONE;

    private static final Map<Type, FieldType> TYPE_BY_TYPE = new HashMap<>();

    static {
        TYPE_BY_TYPE.put(Boolean.class, BOOLEAN);
        TYPE_BY_TYPE.put(boolean.class, BOOLEAN);
        TYPE_BY_TYPE.put(Byte.class, BYTE);
        TYPE_BY_TYPE.put(byte.class, BYTE);
        TYPE_BY_TYPE.put(Character.class, CHAR);
        TYPE_BY_TYPE.put(char.class, CHAR);
        TYPE_BY_TYPE.put(Double.class, DOUBLE);
        TYPE_BY_TYPE.put(double.class, DOUBLE);
        TYPE_BY_TYPE.put(Float.class, FLOAT);
        TYPE_BY_TYPE.put(float.class, FLOAT);
        TYPE_BY_TYPE.put(Integer.class, INT);
        TYPE_BY_TYPE.put(int.class, INT);
        TYPE_BY_TYPE.put(Long.class, LONG);
        TYPE_BY_TYPE.put(long.class, LONG);
        TYPE_BY_TYPE.put(Short.class, SHORT);
        TYPE_BY_TYPE.put(short.class, SHORT);
        TYPE_BY_TYPE.put(String.class, STRING);
    }

    public static FieldType get(Type type) {
        return TYPE_BY_TYPE.getOrDefault(type, NONE);
    }
}
