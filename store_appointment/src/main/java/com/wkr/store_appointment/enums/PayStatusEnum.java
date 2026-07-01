package com.wkr.store_appointment.enums;

public enum PayStatusEnum {

    UNPAID(0, "未支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款");

    private final int code;
    private final String label;

    PayStatusEnum(int code, String label) {
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

    public static String labelOf(Integer value) {
        for (PayStatusEnum item : values()) {
            if (item.matches(value)) {
                return item.getLabel();
            }
        }
        return "未知";
    }
}
