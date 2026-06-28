/**
 * JWT 声明常量：统一令牌生成端与解析端使用的 Claim 名称。
 */
package com.wkr.store_appointment.common;

public final class JwtClaimsConstant {

    public static final String EMP_ID = "empId";
    public static final String USER_ID = "userId";
    public static final String PHONE = "phone";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String ROLE = "role";

    private JwtClaimsConstant() {
    }
}
