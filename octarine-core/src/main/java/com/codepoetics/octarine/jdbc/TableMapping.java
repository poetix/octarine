package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableMapping {

    public static interface PreparedStatementParameterSetter {
        void setParameters(PreparedStatement preparedStatement) throws SQLException;
    }

    public static interface BatchPreparedStatementParameterSetter {
        void setParameters(PreparedStatement preparedStatement, int index) throws SQLException;
        int getBatchSize();
    }

    public static TableMapping forTable(String tableName) {
        return new TableMapping(tableName, HashTreePMap.empty());
    }

    private final String tableName;
    private final PMap<Key<?>, String> columnNameMap;

    private TableMapping(String tableName, PMap<Key<?>, String> columnNameMap) {
        this.tableName = tableName;
        this.columnNameMap = columnNameMap;
    }

    public TableMapping with(Key<?> key, String columnName) {
        return new TableMapping(tableName, columnNameMap.plus(key, columnName));
    }

    public PreparedStatementParameterSetter settingParametersFor(Record record) {
        return ps -> {
            int idx = 1;
            for (Map.Entry<Key<?>, String> entry : columnNameMap.entrySet()) {
                ps.setObject(idx++, record.get(entry.getKey()).orElse(null));
            }
        };
    }

    public BatchPreparedStatementParameterSetter settingParametersFor(List<Record> records) {
         return new BatchPreparedStatementParameterSetter() {
             @Override
             public void setParameters(PreparedStatement preparedStatement, int index) throws SQLException {
                 settingParametersFor(records.get(index)).setParameters(preparedStatement);
             }

             @Override
             public int getBatchSize() {
                 return records.size();
             }
         };
    }

    public PreparedStatement createInsertStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(createInsertSql());
    }

    public String createInsertSql() {
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(",", columnNameMap.values()),
                String.join(",", columnNameMap.values().stream()
                        .map(v -> "?")
                        .collect(Collectors.toList())));
    }
    public TableMapping with(Key<?> key) {
        return with(key, key.name());
    }
}
