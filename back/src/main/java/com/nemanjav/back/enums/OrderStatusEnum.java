package com.nemanjav.back.enums;

public enum OrderStatusEnum implements CodeEnum {
    NEW(0 , "New"),
    FINISHED(1 , "Finished"),
    CANCELED(2 , "Canceled")
    ;

    private int code;
    private String msg;

    OrderStatusEnum(Integer code, String message) {
        this.code = code;
        this.msg = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
