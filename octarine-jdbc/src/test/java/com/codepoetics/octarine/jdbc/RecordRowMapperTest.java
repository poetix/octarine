package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.api.Key;
import com.codepoetics.octarine.testutils.ARecord;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.codepoetics.octarine.Octarine.$;
import static com.codepoetics.octarine.jdbc.ColumnMappers.fromSqlLong;
import static com.codepoetics.octarine.jdbc.ColumnMappers.fromSqlString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordRowMapperTest {

    private static final Key<Long> id = $("id");
    private static final Key<String> name = $("name");

    private static final RecordRowMapper mapper = m ->
            m.add(id, fromSqlLong)
             .add(name, fromSqlString);

    @Test
    public void
    maps_resultset_to_record() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong(1)).thenReturn(23L);
        when(resultSet.getString(2)).thenReturn("Arthur");

        assertThat(mapper.map(resultSet), ARecord.instance().with(id, 23L).with(name, "Arthur"));
    }

    @Test public void
    omits_nulls() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong(1)).thenReturn(23L);
        when(resultSet.getString(2)).thenReturn(null);

        assertThat(mapper.map(resultSet), ARecord.instance().with(id, 23L).without(name));
    }
}
