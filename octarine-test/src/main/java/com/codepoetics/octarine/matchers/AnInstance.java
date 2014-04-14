package com.codepoetics.octarine.matchers;

import com.codepoetics.octarine.records.NamedFunction;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.util.function.Function;

public class AnInstance<T> extends TypeSafeDiagnosingMatcher<T> {

    @Override
    protected boolean matchesSafely(T t, Description description) {
        boolean result = true;
        for (PropertyMatcher<T, ?> pm : matchers) {
            result = result && pm.matchesSafely(t, description);
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("An instance of ").appendValue(klass);
        matchers.forEach(m -> m.describeTo(description));
    }

    public static final class PropertyMatcher<T, V> extends TypeSafeDiagnosingMatcher<T> {

        private final Function<? super T, ? extends V> f;
        private final String name;
        private final Matcher<? extends V> matcher;

        private PropertyMatcher(Function<? super T, ? extends V> f, String name, Matcher<? extends V> matcher) {
            this.f = f;
            this.name = name;
            this.matcher = matcher;
        }

        @Override
        protected boolean matchesSafely(T t, Description description) {
            V value = f.apply(t);
            if (matcher.matches(value)) { return true; }
            description.appendText("\n").appendText(name).appendText(": ");
            matcher.describeMismatch(value, description);
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("\n: ").appendValue(name).appendText(": ");
            matcher.describeTo(description);
        }
    }

    public static <T> AnInstance<T> of(Class<? extends T> klass) {
        return new AnInstance<T>(klass);
    }

    private final Class<? extends T> klass;
    private final PVector<PropertyMatcher<T, ?>> matchers;

    private AnInstance(Class<? extends T> klass) {
        this.klass = klass;
        matchers = TreePVector.empty();
    }

    private AnInstance(Class<? extends T> klass, PVector<PropertyMatcher<T, ?>> matchers) {
        this.klass = klass;
        this.matchers = matchers;
    }

    public <V> AnInstance<T> with(NamedFunction<? super T, ? extends V> f, Matcher<? extends V> matcher) {
        return new AnInstance<T>(klass, matchers.plus(new PropertyMatcher<T, V>(f, f.name(), matcher)));
    }

    public <V> AnInstance<T> with(Function<? super T, ? extends V> f, String name, Matcher<? extends V> matcher) {
        return new AnInstance<T>(klass, matchers.plus(new PropertyMatcher<T, V>(f, name, matcher)));
    }
}
