package com.codepoetics.octarine.testutils;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class IsEmptyMatcher<T> extends TypeSafeDiagnosingMatcher<Iterable<T>> {

    public static <T> IsEmptyMatcher isEmpty() {
        return new IsEmptyMatcher<>();
    }

    @Override
    protected boolean matchesSafely(Iterable<T> ts, Description description) {
        if (ts.iterator().hasNext()) {
            description.appendText("contained ").appendValueList("[", ",", "]", ts);
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is empty");
    }
}
