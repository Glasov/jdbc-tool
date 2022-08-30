package dao.schema;

import java.lang.reflect.Type;

public class SchemaItem {
    private final String fieldName;
    private final SchemaItemType type;
    private final boolean isOptional;

    public SchemaItem(String fieldName, Type type, boolean isOptional) {
        this.fieldName = fieldName;
        this.type = SchemaItemType.get(type);
        this.isOptional = isOptional;
    }

    public String getFieldName() {
        return fieldName;
    }

    public SchemaItemType getType() {
        return type;
    }

    public boolean isOptional() {
        return isOptional;
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
