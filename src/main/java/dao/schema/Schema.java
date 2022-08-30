package dao.schema;

import serializer.FieldInfo;
import serializer.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<SchemaItem> schemaItems;

    private Schema(List<SchemaItem> schemaItems) {
        this.schemaItems = schemaItems;
    }

    public static Schema of(Class<?> clazz) {
        return new Schema(generateFieldSchema(clazz, "", false));
    }

    private static List<SchemaItem> generateFieldSchema(
            Class<?> clazz,
            String namePrefix,
            boolean isParentOptional)
    {
        List<SchemaItem> schema = new ArrayList<>();

        for (FieldInfo field : ReflectionUtils.getFields(clazz)) {
            boolean isOptional = isParentOptional || field.isOptional();
            if (!field.isPrimitive()) {
                Class<?> fieldClass = (Class<?>) field.getType();
                schema.addAll(generateFieldSchema(fieldClass, namePrefix + field.getName() + "_", isOptional));
            } else {
                schema.add(getSchemaItem(field, namePrefix, isOptional));
            }
        }

        return schema;
    }

    private static SchemaItem getSchemaItem(
            FieldInfo field,
            String namePrefix,
            boolean isParentOptional)
    {
        String name = namePrefix + field.getName();
        boolean isOptional = field.isOptional() || isParentOptional;
        return new SchemaItem(name, field.getType(), isOptional);
    }

    @Override
    public String toString() {
        return "Schema{" +
                "schemaItems=" + schemaItems +
                '}';
    }
}
