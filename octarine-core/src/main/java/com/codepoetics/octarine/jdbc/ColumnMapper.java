package com.codepoetics.octarine.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.BiFunction;

public interface ColumnMapper<T> extends BiFunction<ResultSet, Integer, T> {
    @Override default T apply(ResultSet rs, Integer i) {
        try {
            return unsafeApply(rs, i);
        } catch (SQLException e) {
            throw new ColumnMappingException(e);
        }
    }

    T unsafeApply(ResultSet rs, Integer i) throws SQLException;

    static ColumnMapper<Integer> fromInteger = ResultSet::getInt;
    static ColumnMapper<String> fromSqlString = ResultSet::getString;
    static ColumnMapper<Double> fromDouble = ResultSet::getDouble;
    static ColumnMapper<Long> fromSqlLong = ResultSet::getLong;
    static ColumnMapper<Date> fromDate = ResultSet::getDate;
}
