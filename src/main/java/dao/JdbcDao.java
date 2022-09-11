package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dao.schema.FieldType;
import dao.schema.Schema;
import dao.schema.SchemaItem;
import serializer.Deserializer;
import serializer.ObjectDeserializer;
import serializer.SerializedNode;
import serializer.SerializedValue;

public abstract class JdbcDao<TKey, TValue> implements CrudDao<TKey, TValue> {
    private static final String TMP = "__tmp__";

    private final Class<TKey> keyClass;
    private final Class<TValue> valueClass;
    private final Schema schema;
    private final Deserializer keyDeserializer;
    private final Deserializer valueDeserializer;

    public JdbcDao(Class<TKey> keyClass, Class<TValue> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.schema = Schema.of(valueClass);
        this.keyDeserializer = new ObjectDeserializer(keyClass);
        this.valueDeserializer = new ObjectDeserializer(valueClass);
    }

    @Override
    public boolean create(Collection<TValue> values) throws SQLException {
        return execute(prepareCreate(values));
    }

    @Override
    public Collection<TValue> read(Collection<TKey> keys) throws SQLException {
        return request(prepareRead(keys));
    }

    @Override
    public boolean update(Collection<TValue> values) throws SQLException {
        return execute(prepareUpdate(values));
    }

    @Override
    public boolean delete(Collection<TKey> keys) throws SQLException {
        return execute(prepareDelete(keys));
    }

    private List<TValue> request(String query) throws SQLException {
        if (query.isBlank()) return List.of();
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            return toNodes(resultSet).stream()
                    .map(node -> (TValue) valueDeserializer.deserialize(valueClass, node))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }

    private boolean execute(String query) throws SQLException {
        if (query.isBlank()) return false;
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            preparedStatement.execute();
            connection.commit();
            return true;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }

    private String prepareCreate(Collection<TValue> values) {
        if (values.isEmpty()) return "";
        return String.format(
                "INSERT INTO %s (%s) VALUES %s;",
                getName(), schema.joinColumns(), joinValues(values)
        );
    }

    private String prepareRead(Collection<TKey> keys) {
        if (keys.isEmpty()) return "";
        List<SerializedNode> serializedKeys = keys.stream()
                .map(key -> keyDeserializer.serialize(keyClass, key))
                .toList();
        return String.format(
                "SELECT * FROM %s WHERE %s;",
                getName(), generateKeysWhere(serializedKeys)
        );
    }

    private String prepareUpdate(Collection<TValue> values) {
        if (values.isEmpty()) return "";
        return String.format(
                "UPDATE %s SET %s FROM (VALUES %s) AS %s(%s) WHERE %s;",
                getName(), generateUpdateSet(), joinValues(values), TMP, schema.joinColumns(), generateUpdateKeyWhere()
        );
    }

    private String prepareDelete(Collection<TKey> keys) {
        if (keys.isEmpty()) return "";
        List<SerializedNode> serializedKeys = keys.stream()
                .map(key -> keyDeserializer.serialize(keyClass, key))
                .toList();
        return String.format(
                "DELETE FROM %s WHERE %s;",
                getName(), generateKeysWhere(serializedKeys)
        );
    }

    private List<SerializedNode> toNodes(ResultSet resultSet) throws SQLException {
        SerializedValue serializedValue;
        SerializedValue head;
        List<SerializedNode> result = new ArrayList<>();
        while (resultSet.next()) {
            head = SerializedValue.empty();
            serializedValue = head;
            for (int column = 1; column <= schema.columnsCount(); column++) {
                serializedValue.setNext(SerializedValue.of(resultSet.getString(column)));
                serializedValue = serializedValue.getNext();
            }
            head = head.getNext();
            result.add(Objects.nonNull(head) ? head.toNode() : SerializedNode.empty());
        }

        return result;
    }

    private String generateKeysWhere(List<SerializedNode> nodes) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<SerializedNode> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            generateKeyWhere(nodeIterator.next(), stringBuilder);
            if (nodeIterator.hasNext()) stringBuilder.append(" OR ");
        }

        return stringBuilder.toString();
    }

    private void generateKeyWhere(SerializedNode node, StringBuilder stringBuilder) {
        stringBuilder.append("(");
        Iterator<String> columnsIterator = schema.getKeyColumns().iterator();
        while (columnsIterator.hasNext()) {
            stringBuilder.append(columnsIterator.next()).append("=").append(node.next());
            if (columnsIterator.hasNext()) stringBuilder.append(" AND ");
        }

        stringBuilder.append(")");
    }

    private String generateUpdateSet() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> columnIterator = schema.getValueColumns().iterator();
        while (columnIterator.hasNext()) {
            String column = columnIterator.next();
            stringBuilder.append(column).append("=").append(TMP).append(".").append(column);
            if (columnIterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private String generateUpdateKeyWhere() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> columnIterator = schema.getKeyColumns().iterator();
        while (columnIterator.hasNext()) {
            String column = columnIterator.next();
            stringBuilder.append(getName()).append(".").append(column)
                    .append("=")
                    .append(TMP).append(".").append(column);
            if (columnIterator.hasNext()) stringBuilder.append(" AND ");
        }
        return stringBuilder.toString();
    }

    private String joinValues(Collection<TValue> values) {
        return values.stream()
                .map(v -> valueDeserializer.serialize(valueClass, v))
                .map(node -> {
                    StringBuilder stringBuilder = new StringBuilder("(");
                    Iterator<SchemaItem> schemaItemIterator = schema.getFullSchema().iterator();
                    while (schemaItemIterator.hasNext()) {
                        SchemaItem schemaItem = schemaItemIterator.next();
                        if (schemaItem.getType() == FieldType.STRING) stringBuilder.append("'");
                        stringBuilder.append(node.next());
                        if (schemaItem.getType() == FieldType.STRING) stringBuilder.append("'");
                        if (schemaItemIterator.hasNext()) stringBuilder.append(",");
                    }
                    return stringBuilder.append(")").toString();
                })
                .reduce((a, b) -> a + "," + b)
                .orElseThrow();
    }

    public Class<TKey> getKeyClass() {
        return keyClass;
    }

    public Class<TValue> getValueClass() {
        return valueClass;
    }

    protected abstract Connection getConnection() throws SQLException;

    public abstract String getName();
}
