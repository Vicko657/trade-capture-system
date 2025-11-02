package com.technicalchallenge.model;

import java.util.Set;

public enum UserType {

    // Sets each usertype to a operation
    TRADER(Set.of(Operations.CREATE_TRADE.label, Operations.AMEND_TRADE.label,
            Operations.TERMINATE_TRADE.label,
            Operations.CANCEL_TRADE.label)),
    SALES(Set.of(Operations.CREATE_TRADE.label, Operations.AMEND_TRADE.label)),
    MIDDLE_OFFICE(Set.of(Operations.AMEND_TRADE.label, Operations.VIEW_TRADE.label)),
    SUPPORT(Set.of(Operations.VIEW_TRADE.label));

    private final Set<String> authorisedOperation;

    UserType(Set<String> authorisedOperation) {
        this.authorisedOperation = authorisedOperation;
    }

    public Set<String> isAllowed() {
        return authorisedOperation;
    }

}
