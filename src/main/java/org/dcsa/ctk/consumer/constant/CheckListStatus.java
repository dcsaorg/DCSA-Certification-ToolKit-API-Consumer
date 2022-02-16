package org.dcsa.ctk.consumer.constant;

public enum CheckListStatus {
    COVERED("Covered"),
    NOT_COVERED("Not Covered");
    private String name;

    CheckListStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
