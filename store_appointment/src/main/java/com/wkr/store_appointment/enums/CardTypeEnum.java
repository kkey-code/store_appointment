package com.wkr.store_appointment.enums;

public enum CardTypeEnum {

    TIME_CARD("次卡"),
    CARE_CARD("护理卡"),
    MEMBER_CARD("会员卡"),
    COURSE_CARD("疗程卡");

    private final String label;

    CardTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
