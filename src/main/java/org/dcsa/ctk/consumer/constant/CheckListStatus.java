package org.dcsa.ctk.consumer.constant;

public enum CheckListStatus {
    CONFRONTED("CONFRONTED"),
    UNCONFRONTED("UNCONFRONTED");
    private String name;

    CheckListStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
