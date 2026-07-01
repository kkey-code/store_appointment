package com.wkr.store_appointment.enums;

public enum AppointmentStatusEnum {

    PENDING(0, "待确认"),
    CONFIRMED(1, "已确认"),
    COMPLETED(2, "已完成"),
    CANCELED(3, "已取消");

    private final int code;
    private final String label;

    AppointmentStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public boolean matches(Integer value) {
        return value != null && value == code;
    }
}
