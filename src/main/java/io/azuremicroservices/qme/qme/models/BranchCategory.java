package io.azuremicroservices.qme.qme.models;

public enum BranchCategory {
    HEALTHCARE("fa-clinic-medical"),
    FINANCE("fa-piggy-bank"),
    DINING("fa-utensils"),
    TECHNOLOGY("fa-sim-card"),
    RETAIL("fa-shopping-bag"),
    GOVERNMENTAL("fa-landmark");

    private final String displayValue;
    private final String iconClass;
    
    BranchCategory(String iconClass) {
        // Generalized constructor that converts capitalized enum values to TitleCase
        StringBuilder sb = new StringBuilder();

        for (String word : this.name().split("_")) {
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase()).append(" ");
        }

        this.displayValue = sb.toString().trim();
        this.iconClass = iconClass;
    }

    public String getDisplayValue() { return displayValue; }
    public String getIconClass() { return iconClass; }
}
