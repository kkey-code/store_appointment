package com.wkr.store_appointment.enums;

public enum OrderStatusEnum {

    PENDING(0, "待服务"),
    COMPLETED(1, "已完成"),
    CANCELED(2, "已取消");

    private final int code;
    private final String label;

    OrderStatusEnum(int code, String label) {
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
        for (OrderStatusEnum item : values()) {
            if (item.matches(value)) {
                return item.getLabel();
            }
        }
        return "未知";
    }
}
