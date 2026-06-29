/**
 * 请求上下文工具：在线程范围内保存当前登录用户标识。
 */
package com.wkr.store_appointment.common;

public final class BaseContext {

    private static final ThreadLocal<Long> CURRENT_ID = new ThreadLocal<>();

    private BaseContext() {
    }

    public static void setCurrentId(Long id) {
        CURRENT_ID.set(id);
    }

    public static Long getCurrentId() {
        return CURRENT_ID.get();
    }

    public static void removeCurrentId() {
        CURRENT_ID.remove();
    }
}
