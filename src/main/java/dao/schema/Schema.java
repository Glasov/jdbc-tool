package dao.schema;

import java.util.ArrayList;
import java.util.List;

import dao.schema.annotations.SchemaKey;
import serializer.FieldInfo;
import serializer.ReflectionUtils;

public class Schema {
    private final List<SchemaItem> valueItems;
    private final List<SchemaItem> keyItems;
    private final List<SchemaItem> fullSchema;

    private Schema(List<SchemaItem> fullSchema) {
        this.fullSchema = fullSchema;
        this.valueItems = fullSchema.stream().filter(si -> !si.isKey()).toList();
        this.keyItems = fullSchema.stream().filter(SchemaItem::isKey).toList();
    }

    public static Schema of(Class<?> clazz) {
        return new Schema(generateFieldSchema(
                clazz,
                "",
                false,
                false)
        );
    }

    private static List<SchemaItem> generateFieldSchema(
            Class<?> clazz,
            String namePrefix,
            boolean isParentOptional,
            boolean isParentKey)
    {
        List<SchemaItem> schema = new ArrayList<>();

        for (FieldInfo field : ReflectionUtils.getFields(clazz)) {
            boolean isOptional = isParentOptional || field.isOptional();
            boolean isKey = field.hasAnnotation(SchemaKey.class) || isParentKey;
            if (!field.isPrimitive()) {
                Class<?> fieldClass = (Class<?>) field.getType();
                schema.addAll(generateFieldSchema(
                        fieldClass,
                        namePrefix + field.getName() + "_",
                        isOptional,
                        isKey)
                );
            } else {
                schema.add(getSchemaItem(field, namePrefix, isOptional, isKey));
            }
        }

        return schema;
    }

    private static SchemaItem getSchemaItem(
            FieldInfo field,
            String namePrefix,
            boolean isParentOptional,
            boolean isKey)
    {
        String name = namePrefix + field.getName();
        boolean isOptional = field.isOptional() || isParentOptional;
        return new SchemaItem(name, field.getType(), isOptional, isKey);
    }

    public List<String> getAllColumns() {
        return fullSchema.stream().map(SchemaItem::getFieldName).toList();
    }

    public List<String> getKeyColumns() {
        return keyItems.stream().map(SchemaItem::getFieldName).toList();
    }

    public List<String> getValueColumns() {
        return valueItems.stream().map(SchemaItem::getFieldName).toList();
    }

    public int columnsCount() {
        return fullSchema.size();
    }

    public String joinColumns() {
        return getAllColumns().stream().reduce((a, b) -> a + "," + b).orElseThrow();
    }

    public List<SchemaItem> getFullSchema() {
        return fullSchema;
    }
}
