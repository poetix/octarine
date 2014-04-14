package com.codepoetics.octarine.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Optional;

public class Present<T> extends TypeSafeDiagnosingMatcher<Optional<T>> {
    public static <T> Present<T> and(Matcher<T> matcher) {
        return new Present<T>(matcher);
    }

    private final Matcher<T> matcher;
    private Present(Matcher<T> matcher) {
        this.matcher = matcher;
    }


    @Override
    protected boolean matchesSafely(Optional<T> tOptional, Description description) {
        if (!tOptional.isPresent()) {
            description.appendText("was absent");
            return false;
        }
        T value = tOptional.get();
        if (matcher.matches(value)) { return true; }
        matcher.describeMismatch(value, description);
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("present and ");
        matcher.describeTo(description);
    }
}
