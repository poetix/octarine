package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class KeyMatchingSpliteratorTest {

    @Test public void
    returnsPairsWhereAllKeysMatch() {
        Map<String, Integer> left = new TreeMap<>();
        left.put("b", 2);
        left.put("a", 1);
        left.put("c", 3);

        Map<String, String> right = new TreeMap<>();
        right.put("a", "apple");
        right.put("c", "carrot");
        right.put("b", "banana");

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(1, "apple"), T2.of(2, "banana"), T2.of(3, "carrot")));
    }

    @Test public void
    returnsPairsWhereKeysMismatch() {
        Map<String, Integer> left = new TreeMap<>();
        left.put("b", 2);
        left.put("a", 1);
        left.put("d", 4);

        Map<String, String> right = new TreeMap<>();
        right.put("a", "apple");
        right.put("c", "carrot");
        right.put("b", "banana");

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(1, "apple"), T2.of(2, "banana"), T2.of(0, "carrot"), T2.of(4, "")));
    }

    @Test public void
    returnsPairsWhereMoreKeysOnLeft() {
        Map<String, Integer> left = new TreeMap<>();
        left.put("a", 1);
        left.put("b", 2);
        left.put("c", 3);

        Map<String, String> right = new TreeMap<>();
        right.put("b", "banana");
        right.put("a", "apple");

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(1, "apple"), T2.of(2, "banana"), T2.of(3, "")));
    }

    @Test public void
    returnsPairsWhereMoreKeysOnRight() {
        Map<String, Integer> left = new TreeMap<>();
        left.put("a", 1);
        left.put("b", 2);

        Map<String, String> right = new TreeMap<>();
        right.put("b", "banana");
        right.put("a", "apple");
        right.put("c", "carrot");

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(1, "apple"), T2.of(2, "banana"), T2.of(0, "carrot")));
    }

    @Test public void
    returnsPairsWhereLeftEmpty() {
        Map<String, Integer> left = new TreeMap<>();

        Map<String, String> right = new TreeMap<>();
        right.put("b", "banana");
        right.put("a", "apple");
        right.put("c", "carrot");

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(0, "apple"), T2.of(0, "banana"), T2.of(0, "carrot")));
    }

    @Test public void
    returnsPairsWhereRightEmpty() {
        Map<String, Integer> left = new TreeMap<>();
        left.put("b", 2);
        left.put("a", 1);
        left.put("c", 3);

        Map<String, String> right = new TreeMap<>();

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples, hasItems(T2.of(1, ""), T2.of(2, ""), T2.of(3, "")));
    }


    @Test public void
    returnsEmptyWhereBothEmpty() {
        Map<String, Integer> left = new TreeMap<>();
        Map<String, String> right = new TreeMap<>();

        KeyMatchingSpliterator<String, Integer, String> spliterator = KeyMatchingSpliterator.over(
                Comparator.naturalOrder(),
                left.entrySet().spliterator(),
                right.entrySet().spliterator(),
                0,
                "");

        List<T2<Integer, String>> tuples = StreamSupport.stream(spliterator, false).collect(Collectors.toList());
        assertThat(tuples.size(), is(0));
    }
}
