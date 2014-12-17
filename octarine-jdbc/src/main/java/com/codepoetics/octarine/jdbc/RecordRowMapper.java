package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.keys.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.keys.Value;
import com.codepoetics.octarine.validation.Schema;
import com.codepoetics.octarine.validation.Valid;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface RecordRowMapper extends RowMapper<Record> {

    ColumnMappings configure(ColumnMappings empty);

    default String columnNames() {
        StringBuilder sb = new StringBuilder();
        configure(new ColumnMappings() {
            @Override
            public <T> ColumnMappings add(Key<? super T> key, String columnName, ColumnMapper<? extends T> columnMapper) {
                if (sb.length() != 0) { sb.append(", "); }
                sb.append(columnName);
                return this;
            }
        });
        return sb.toString();
    }

    @Override
    default Record map(ResultSet resultSet) {
        List<Value> columnValues = new LinkedList<>();
        configure(new ColumnMappings() {
            private int index = 1;

            @Override
            public <T> ColumnMappings add(Key<? super T> key, String columnName, ColumnMapper<? extends T> columnMapper) {
                Optional<T> maybeColumnValue = Optional.ofNullable(columnMapper.apply(resultSet, index));
                index += 1;
                maybeColumnValue.ifPresent(columnValue -> columnValues.add(key.of(columnValue)));
                return this;
            }
        });
        return Record.of(columnValues);
    }

    default <T> RowMapper<Valid<T>> validAgainst(Schema<T> schema) {
        return new RowMapper<Valid<T>>() {
            @Override
            public Valid<T> map(ResultSet resultSet) {
                return schema.extract(RecordRowMapper.this.map(resultSet));
            }
        };
    }

    interface ColumnMappings {
        <T> ColumnMappings add(Key<? super T> key, String columnName, ColumnMapper<? extends T> columnMapper);
        default <T> ColumnMappings add(Key<? super T> key, ColumnMapper<? extends T> columnMapper) {
            return add(key, key.name(), columnMapper);
        }
    }
}
