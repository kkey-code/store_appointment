package com.wkr.store_appointment.enums;

public enum PaymentMethodEnum {

    WECHAT("微信"),
    ALIPAY("支付宝"),
    CASH("现金"),
    TIME_CARD("次卡");

    private final String label;

    PaymentMethodEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
