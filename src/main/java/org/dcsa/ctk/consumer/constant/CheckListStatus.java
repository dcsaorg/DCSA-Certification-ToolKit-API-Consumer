package org.dcsa.ctk.consumer.constant;

public enum CheckListStatus {
    CONFORMANT("CONFORMANT"),
    NOT_CONFORMANT("NOT CONFORMANT");
    private String name;

    CheckListStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
