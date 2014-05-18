package com.codepoetics.octarine.jdbc;

import java.sql.ResultSet;

public interface RowMapper<T> {

    T map(ResultSet resultSet);

}
