package com.wkr.store_appointment.enums;

public enum CustomerLevelEnum {

    NORMAL("普通"),
    SILVER("银卡"),
    GOLD("金卡"),
    VIP("VIP");

    private final String label;

    CustomerLevelEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
