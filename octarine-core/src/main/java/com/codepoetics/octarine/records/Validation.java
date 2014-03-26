package com.codepoetics.octarine.records;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public interface Validation<T> extends Supplier<Valid<T>> {

    boolean isValid();
    List<String> validationErrors();

    static <T> Validation<T> valid(Valid<T> validValue) {
        return new Validation<T>() {

            @Override public boolean isValid() { return true; }

            @Override
            public List<String> validationErrors() {
                return Collections.emptyList();
            }

            @Override
            public Valid<T> get() {
                return validValue;
            }
        };
    }

    static <T> Validation<T> invalid(List<String> validationErrors) {
        return new Validation<T>() {

            @Override public boolean isValid() { return false; }

            @Override
            public List<String> validationErrors() {
                return validationErrors;
            }

            @Override
            public Valid<T> get() {
                throw new IllegalStateException("Cannot get valid value from failed validation");
            }
        };
    }
}
