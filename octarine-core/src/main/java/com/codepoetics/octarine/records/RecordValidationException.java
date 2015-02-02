package com.codepoetics.octarine.records;

import java.util.List;

public final class RecordValidationException extends RuntimeException {

    private final List<String> validationErrors;

    public RecordValidationException(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public <R> Validation<R> toValidation() {
        return Validation.invalid(validationErrors);
    }
}
