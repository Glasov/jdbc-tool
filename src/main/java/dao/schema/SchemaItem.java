package dao.schema;

import java.lang.reflect.Type;

public class SchemaItem {
    private final String fieldName;
    private final FieldType type;
    private final boolean isOptional;
    private final boolean isKey;

    public SchemaItem(String fieldName, Type type, boolean isOptional, boolean isKey) {
        this.fieldName = fieldName;
        this.type = FieldType.get(type);
        this.isOptional = isOptional;
        this.isKey = isKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldType getType() {
        return type;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public boolean isKey() {
        return isKey;
    }

    @Override
    public String toString() {
        return "SchemaItem{" +
                "fieldName='" + fieldName + '\'' +
                ", type=" + type +
                ", isOptional=" + isOptional +
                '}';
    }
}
