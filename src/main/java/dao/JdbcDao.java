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

import dao.schema.Schema;
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            return toNodes(resultSet).stream()
                    .map(node -> (TValue) valueDeserializer.deserialize(valueClass, node))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return List.of();
    }

    private boolean execute(String query) throws SQLException {
        if (query.isBlank()) return false;
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);
            preparedStatement.execute();
            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return false;
    }

    private String prepareCreate(Collection<TValue> values) {
        if (values.isEmpty()) return "";
        String serializedValues = values.stream()
                .map(v -> valueDeserializer.serialize(valueClass, v))
                .map(SerializedNode::join)
                .reduce((a, b) -> a + "," + b)
                .orElseThrow();
        return String.format(
                "INSERT INTO %s (%s) VALUES %s;",
                getName(), schema.joinColumns(), serializedValues
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
        String serializedValues = values.stream()
                .map(v -> valueDeserializer.serialize(valueClass, v))
                .map(SerializedNode::join)
                .reduce((a, b) -> a + "," + b)
                .orElseThrow();
        return String.format(
                "UPDATE %s SET %s FROM (VALUES %s) AS %s(%s) WHERE %s;",
                getName(), generateUpdateSet(), serializedValues, TMP, schema.joinColumns(), generateUpdateKeyWhere()
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
        List<SerializedNode> result = new ArrayList<>();
        while (resultSet.next()) {
            serializedValue = SerializedValue.empty();
            for (int column = 0; column < schema.columnsCount(); column++) {
                serializedValue.setNext(SerializedValue.of(resultSet.getString(column)));
            }
            serializedValue = serializedValue.getNext();
            result.add(Objects.nonNull(serializedValue) ? serializedValue.toNode() : SerializedNode.empty());
        }

        return result;
    }

    public String generateKeysWhere(List<SerializedNode> nodes) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<SerializedNode> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            generateKeyWhere(nodeIterator.next(), stringBuilder);
            if (nodeIterator.hasNext()) stringBuilder.append(" OR ");
        }

        return stringBuilder.toString();
    }

    public void generateKeyWhere(SerializedNode node, StringBuilder stringBuilder) {
        stringBuilder.append("(");
        Iterator<String> columnsIterator = schema.getKeyColumns().iterator();
        while (columnsIterator.hasNext()) {
            stringBuilder.append(columnsIterator.next()).append("=").append(node.next());
            if (columnsIterator.hasNext()) stringBuilder.append("AND ");
        }

        stringBuilder.append(")");
    }

    public String generateUpdateSet() {
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

    public String generateUpdateKeyWhere() {
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

    public Class<TKey> getKeyClass() {
        return keyClass;
    }

    public Class<TValue> getValueClass() {
        return valueClass;
    }

    protected abstract Connection getConnection() throws SQLException;

    public abstract String getName();
}
