package io.azuremicroservices.qme.qme.models;

public enum BranchCategory {
    HEALTHCARE,
    FINANCE,
    DINING,
    TECHNOLOGY,
    RETAIL,
    GOVERNMENTAL;

    private final String displayValue;

    BranchCategory() {
        // Generalized constructor that converts capitalized enum values to TitleCase
        StringBuilder sb = new StringBuilder();

        for (String word : this.name().split("_")) {
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
        }

        this.displayValue = sb.toString().trim();
    }

    public String getDisplayValue() { return displayValue; }
}
