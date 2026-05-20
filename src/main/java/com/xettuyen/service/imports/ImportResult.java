package com.xettuyen.service.imports;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {

    private final List<String> errors = new ArrayList<>();

    public void addError(int row, String reason) {
        errors.add("Dòng " + row + ": " + reason);
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}