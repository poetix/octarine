package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.records.*;
import com.codepoetics.validation.Schema;
import com.codepoetics.validation.Valid;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public interface RecordRowMapper extends RowMapper<Record> {
    interface ColumnMappings {
        <T> ColumnMappings add(Key<? super T> key, ColumnMapper<? extends T> columnMapper);
    }
    ColumnMappings inject(ColumnMappings empty);
    @Override
    default Record map(ResultSet resultSet) {
        List<Value> columnValues = new LinkedList<>();
        inject(new ColumnMappings() {
            private int index = 1;

            @Override
            public <T> ColumnMappings add(Key<? super T> key, ColumnMapper<? extends T> columnMapper) {
                T columnValue = columnMapper.apply(resultSet, index);
                index += 1;
                columnValues.add(key.of(columnValue));
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
}
