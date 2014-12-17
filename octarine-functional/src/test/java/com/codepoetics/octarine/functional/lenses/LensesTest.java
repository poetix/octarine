package com.codepoetics.octarine.functional.lenses;

import com.codepoetics.octarine.functional.morphisms.Bijection;
import org.junit.Test;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LensesTest {

    private static final Lens<PMap<String, String>, String> aIsFor = Lens.intoPMap("a");
    private static final Bijection<String, Character[]> stringToChars = Bijection.of(
            (String s) -> {
                Character[] characters = new Character[s.length()];
                char[] chars = s.toCharArray();
                for (int i = 0; i < s.length(); i++) {
                    characters[i] = chars[i];
                }
                return characters;
            },
            (Character[] cs) -> {
                StringBuilder sb = new StringBuilder();
                Arrays.stream(cs).forEach(sb::append);
                return sb.toString();
            }
    );
    private static final Lens<String, Character> thirdChar = stringToChars.overSource(Lens.<Character>intoArray(2));

    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("a", "apple");
    }

    private static final PMap<String, String> pmap = HashTreePMap.from(map);

    @Test
    public void
    lenses_can_be_composed_out_of_functions() {
        assertThat(aIsFor.apply(pmap), equalTo("apple"));
        assertThat(aIsFor.set(pmap, "artichoke").get("a"), equalTo("artichoke"));
    }

    @Test
    public void
    lenses_compose_with_bijections() {
        assertThat(thirdChar.apply("Hello World"), equalTo('l'));
        assertThat(thirdChar.set("Hello World", 'f'), equalTo("Heflo World"));
    }

    @Test
    public void
    lenses_can_be_joined_together() {
        Lens<PMap<String, String>, Character> thirdCharOfA = aIsFor.join(thirdChar);

        assertThat(thirdCharOfA.apply(pmap), equalTo('p'));
        assertThat(thirdCharOfA.set(pmap, 's').get("a"), equalTo("apsle"));
    }

    @Test public void
    can_behave_as_an_optional_lens_converting_null_to_empty() {
        String[] strings = new String[] { "a string", null, "another string" };
        OptionalLens<String[], String> firstItem = Lens.<String>intoArray(0).asOptional();
        OptionalLens<String[], String> secondItem = Lens.<String>intoArray(1).asOptional();

        assertThat(firstItem.get(strings), equalTo(Optional.of("a string")));
        assertThat(secondItem.get(strings), equalTo(Optional.empty()));
    }

    @Test public void
    can_create_an_injector() {
        String[] strings = new String[] { "a string", null, "another string" };
        Lens<String[], String> secondItem = Lens.<String>intoArray(1);

        Function<String[], String[]> injector = secondItem.inject("the missing string");

        assertThat(injector.apply(strings), equalTo(new String[]{"a string", "the missing string", "another string"}));
    }

    @Test public void
    can_create_an_inflector() {
        String[] strings = new String[] { "cow", "duck" };
        Lens<String[], String> secondItem = Lens.<String>intoArray(1);

        Function<String[], String[]> secondItemPluraliser = secondItem.inflect(s -> s + "s");

        assertThat(secondItemPluraliser.apply(strings), equalTo(new String[]{"cow", "ducks"}));
    }
}
