package com.codepoetics.octarine.lenses;

import com.codepoetics.octarine.morphisms.Bijection;
import org.junit.Test;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LensesTest {

    private static final Lens<PMap<String, String>, String> aIsFor = Lens.intoPMap("a");
    private static final Lens<String, Character> thirdChar = Lens.<Character>intoArray(2).compose(Bijection.of(
        (Character[] cs) -> {
                StringBuilder sb = new StringBuilder();
                Arrays.stream(cs).forEach(sb::append);
                return sb.toString();
            },
        (String s) -> {
            Character[] characters = new Character[s.length()];
            char[] chars = s.toCharArray();
            for (int i = 0; i<s.length(); i++) {
               characters[i] = chars[i];
            }
            return characters;
        }
    ));

    private static final Map<String, String> map = new HashMap<>();
    static { map.put("a", "apple"); }
    private static final PMap<String, String> pmap = HashTreePMap.from(map);

    @Test public void
    lenses_can_be_composed_out_of_functions() {
        assertThat(aIsFor.apply(pmap).get(), equalTo("apple"));
        assertThat(aIsFor.apply(pmap).apply("artichoke").get("a"), equalTo("artichoke"));
    }

    @Test public void
    lenses_compose_with_bijections() {
        assertThat(thirdChar.apply("Hello World").get(), equalTo('l'));
        assertThat(thirdChar.apply("Hello World").apply('f'), equalTo("Heflo World"));
    }

    @Test public void
    lenses_can_be_joined_together() {
        Lens<PMap<String, String>, Character> thirdCharOfA = aIsFor.andThen(thirdChar);

        assertThat(thirdCharOfA.apply(pmap).get(), equalTo('p'));
        assertThat(thirdCharOfA.apply(pmap).apply('s').get("a"), equalTo("apsle"));
    }
}
