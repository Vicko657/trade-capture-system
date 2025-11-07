package com.technicalchallenge.model;

public enum Operations {
    CREATE_TRADE("CREATE_TRADE"), AMEND_TRADE("AMEND_TRADE"), TERMINATE_TRADE("TERMINATE_TRADE"),
    CANCEL_TRADE("CANCEL_TRADE"),
    VIEW_TRADE("VIEW_TRADE");

    public final String label;

    private Operations(String label) {
        this.label = label;
    }

}
