package com.codepoetics.octarine.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

public final class ColumnMappers {
    private ColumnMappers() { }

    public static final ColumnMapper<Integer> fromInteger = ResultSet::getInt;
    public static final ColumnMapper<String> fromSqlString = ResultSet::getString;
    public static final ColumnMapper<Double> fromDouble = ResultSet::getDouble;
    public static final ColumnMapper<Long> fromSqlLong = ResultSet::getLong;
    public static final ColumnMapper<Date> fromDate = ResultSet::getDate;
    public static final ColumnMapper<Boolean> fromBoolean = ResultSet::getBoolean;
    public static final ColumnMapper<BigDecimal> fromDecimal = ResultSet::getBigDecimal;
}
