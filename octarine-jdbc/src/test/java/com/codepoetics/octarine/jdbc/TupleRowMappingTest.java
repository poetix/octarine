package com.codepoetics.octarine.jdbc;

import com.codepoetics.octarine.functional.tuples.T5;
import com.codepoetics.octarine.functional.tuples.TupleLens;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TupleRowMappingTest {

    private static final TupleLens<T5<String, String, Date, Long, String>, String> id = T5.first();
    private static final TupleLens<T5<String, String, Date, Long, String>, String> name = T5.second();
    private static final TupleLens<T5<String, String, Date, Long, String>, Date> dob = T5.third();
    private static final TupleLens<T5<String, String, Date, Long, String>, Long> score = T5.fourth();
    private static final TupleLens<T5<String, String, Date, Long, String>, String> comments = T5.fifth();

    public static final class ResultSetReader {
        private ResultSetReader() {
        }

        public static interface ColumnReader<T> {
            T apply(ResultSet r, int index) throws SQLException;
            default T safeApply(ResultSet r, int index) {
                try {
                    return apply(r, index);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            default Function<ResultSet, T> forIndex(int index) {
                return r -> safeApply(r, index);
            }
        }

        public static <A, B, C, D, E> Function<ResultSet, T5<A, B, C, D, E>> of(
                ColumnReader<A> a,
                ColumnReader<B> b,
                ColumnReader<C> c,
                ColumnReader<D> d,
                ColumnReader<E> e) {
            return T5.unpacker(a.forIndex(0), b.forIndex(1), c.forIndex(2), d.forIndex(3), e.forIndex(4));
        }

    }
    private static final Function<ResultSet, T5<String, String, Date, Long, String>> reader = ResultSetReader.of(
            ResultSet::getString,
            ResultSet::getString,
            ResultSet::getDate,
            ResultSet::getLong,
            ResultSet::getString
    );

    @Test public void
    mapResultSetToTuple() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getString(0)).thenReturn("id1");
        when(resultSet.getString(1)).thenReturn("Arthur Putey");
        when(resultSet.getDate(2)).thenReturn(new java.sql.Date(1000));
        when(resultSet.getLong(3)).thenReturn(56L);
        when(resultSet.getString(4)).thenReturn("Le sange est dans l'arbre");

        T5<String, String, Date, Long, String> tuple = reader.apply(resultSet);

        assertThat(id.extract(tuple), equalTo("id1"));
        assertThat(name.extract(tuple), equalTo("Arthur Putey"));
        assertThat(dob.extract(tuple), equalTo((new java.sql.Date(1000))));
        assertThat(score.extract(tuple), equalTo(56L));
        assertThat(comments.extract(tuple), equalTo("Le sange est dans l'arbre"));
    }
}
