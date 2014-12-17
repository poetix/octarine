package com.codepoetics.octarine.jdbc;

import java.sql.SQLException;

class ColumnMappingException extends RuntimeException {
    public ColumnMappingException(SQLException e) {
        super(e);
    }
}
