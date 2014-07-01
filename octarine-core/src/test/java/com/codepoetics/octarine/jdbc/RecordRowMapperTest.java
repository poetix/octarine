package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.codepoetics.octarine.jdbc.ColumnMappers.fromSqlLong;
import static com.codepoetics.octarine.jdbc.ColumnMappers.fromSqlString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecordRowMapperTest {

    private static final Key<Long> id = Key.named("id");
    private static final Key<String> name = Key.named("name");

    private static final RecordRowMapper mapper = m ->
            m.add(id, fromSqlLong)
                    .add(name, fromSqlString);

    @Test
    public void
    maps_resultset_to_record() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong(1)).thenReturn(23L);
        when(resultSet.getString(2)).thenReturn("Arthur");

        Record record = mapper.map(resultSet);
        assertThat(id.extract(record), equalTo(23L));
        assertThat(name.extract(record), equalTo("Arthur"));
    }
}
