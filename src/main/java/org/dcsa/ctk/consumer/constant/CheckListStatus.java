package org.dcsa.ctk.consumer.constant;

public enum CheckListStatus {
    COVERED("COVERED"),
    NOT_COVERED("NOT COVERED");
    private String name;

    CheckListStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
