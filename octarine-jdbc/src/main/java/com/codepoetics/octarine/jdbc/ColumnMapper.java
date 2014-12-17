package com.codepoetics.octarine.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;

public interface ColumnMapper<T> extends BiFunction<ResultSet, Integer, T> {

    @Override
    default T apply(ResultSet rs, Integer i) {
        try {
            return unsafeApply(rs, i);
        } catch (SQLException e) {
            throw new ColumnMappingException(e);
        }
    }

    T unsafeApply(ResultSet rs, Integer i) throws SQLException;
}
