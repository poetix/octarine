package com.codepoetics.octarine.joins;

import com.codepoetics.octarine.functional.tuples.T2;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class IndexTest {

    private final Function<String, Character> indexOnFirstChar = s -> s.charAt(0);

    @Test public void
    oneToManyJoin() {
        Index<Character, String> left = Index.on(Stream.of("banana", "apple", "carrot"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);

        assertThat(left.oneToMany(right).collect(Collectors.toList()), hasItems(
                T2.of("apple", setOf("apex", "aardvaark")),
                T2.of("banana", setOf("butane", "banter")),
                T2.of("carrot", setOf("capybara", "catamite"))));
    }

    @Test public void
    strictOneToManyJoin() {
        Index<Character, String> left = Index.on(Stream.of("banana", "apple", "carrot"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);

        assertThat(left.strictOneToMany(right).collect(Collectors.toList()), hasItems(
                T2.of("apple", setOf("apex", "aardvaark")),
                T2.of("banana", setOf("butane", "banter")),
                T2.of("carrot", setOf("capybara", "catamite"))));
    }

    @Test(expected = IllegalArgumentException.class) public void
    strictOneToManyJoinWithMissingLeft() {
        Index<Character, String> left = Index.on(Stream.of("banana", "carrot"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);

        left.strictOneToMany(right).collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class) public void
    strictOneToManyJoinWithDuplicateLeft() {
        Index<Character, String> left = Index.on(Stream.of("banana", "apple", "antelope", "carrot"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);

        left.strictOneToMany(right).collect(Collectors.toList());
    }

    @Test public void
    manyToOneJoin() {
        Index<Character, String> left = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("banana", "apple", "carrot"), indexOnFirstChar);

        assertThat(left.manyToOne(right).collect(Collectors.toList()), hasItems(
                T2.of("aardvaark", "apple"),
                T2.of("apex", "apple"),
                T2.of("butane", "banana"),
                T2.of("banter", "banana"),
                T2.of("capybara", "carrot"),
                T2.of("catamite", "carrot")));
    }

    @Test public void
    strictManyToOneJoin() {
        Index<Character, String> left = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("banana", "apple", "carrot"), indexOnFirstChar);

        assertThat(left.strictManyToOne(right).collect(Collectors.toList()), hasItems(
                T2.of("aardvaark", "apple"),
                T2.of("apex", "apple"),
                T2.of("butane", "banana"),
                T2.of("banter", "banana"),
                T2.of("capybara", "carrot"),
                T2.of("catamite", "carrot")));
    }

    @Test(expected = IllegalArgumentException.class) public void
    strictManyToOneJoinWithMissingRight() {
        Index<Character, String> left = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("banana", "carrot"), indexOnFirstChar);

        left.strictManyToOne(right).collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class) public void
    strictManyToOneJoinWithDuplicateRight() {
        Index<Character, String> left = Index.on(Stream.of("capybara", "aardvaark", "catamite", "banter", "apex", "butane"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("banana", "aphoristic", "anomie", "carrot"), indexOnFirstChar);

        left.strictManyToOne(right).collect(Collectors.toList());
    }

    @Test public void
    oneToOneJoin() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "apple", "banana"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("artifice", "catamorphism", "banausic"), indexOnFirstChar);

        assertThat(left.strictOneToOne(right).collect(Collectors.toList()), hasItems(
                T2.of("apple", "artifice"),
                T2.of("banana", "banausic"),
                T2.of("carrot", "catamorphism")));
    }

    @Test(expected = IllegalArgumentException.class) public void
    oneToOneJoinWithMissingLeft() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("artifice", "catamorphism", "banausic"), indexOnFirstChar);

        left.strictOneToOne(right).collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class) public void
    oneToOneJoinWithDuplicateLeft() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "apple", "banana", "aardvaark"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("artifice", "catamorphism", "banausic"), indexOnFirstChar);

        left.strictOneToOne(right).collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class) public void
    oneToOneJoinWithMissingRight() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana", "apple"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("catamorphism", "banausic"), indexOnFirstChar);

        left.strictOneToOne(right).collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class) public void
    oneToOneJoinWithDuplicateRight() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana", "apple"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("catamorphism", "bilious", "artifice", "banausic"), indexOnFirstChar);

        left.strictOneToOne(right).collect(Collectors.toList());
    }

    @Test public void
    leftOuterJoin() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana", "apple"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("catamorphism", "banausic", "ballistic", "diagonally"), indexOnFirstChar);

        assertThat(left.leftOuterJoin(right).collect(Collectors.toList()), hasItems(
                T2.of("apple", Optional.empty()),
                T2.of("banana", Optional.of("banausic")),
                T2.of("banana", Optional.of("ballistic")),
                T2.of("carrot", Optional.of("catamorphism"))));
    }

    @Test public void
    rightOuterJoin() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana", "apple"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("catamorphism", "banausic", "ballistic", "dirigible"), indexOnFirstChar);

        assertThat(left.rightOuterJoin(right).collect(Collectors.toList()), hasItems(
                T2.of(Optional.of("banana"), "banausic"),
                T2.of(Optional.of("banana"), "ballistic"),
                T2.of(Optional.of("carrot"), "catamorphism"),
                T2.of(Optional.empty(), "dirigible")));
    }

    @Test public void
    outerJoin() {
        Index<Character, String> left = Index.on(Stream.of("carrot", "banana", "apple"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("catamorphism", "banausic", "ballistic", "dirigible"), indexOnFirstChar);

        assertThat(left.outerJoin(right).collect(Collectors.toList()), hasItems(
                T2.of(Optional.of("apple"), Optional.empty()),
                T2.of(Optional.of("banana"), Optional.of("banausic")),
                T2.of(Optional.of("banana"), Optional.of("ballistic")),
                T2.of(Optional.of("carrot"), Optional.of("catamorphism")),
                T2.of(Optional.empty(), Optional.of("dirigible"))));
    }

    @Test public void
    innerJoin() {
        Index<Character, String> left = Index.on(Stream.of("apple", "banana", "bazooka", "carrot", "catalepsy"), indexOnFirstChar);
        Index<Character, String> right = Index.on(Stream.of("atrophy", "anomie", "ballistic", "banausic", "copious"), indexOnFirstChar);

        assertThat(left.innerJoin(right).collect(Collectors.toList()), hasItems(
                T2.of("apple", "atrophy"),
                T2.of("apple", "anomie"),
                T2.of("banana", "ballistic"),
                T2.of("banana", "banausic"),
                T2.of("bazooka", "ballistic"),
                T2.of("bazooka", "banausic"),
                T2.of("carrot", "copious"),
                T2.of("catalepsy", "copious")));
    }

    private <T> Set<T> setOf(T...items) {
        return Stream.of(items).collect(Collectors.toSet());
    }
}
