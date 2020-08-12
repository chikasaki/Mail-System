package com.yao.enums;

import lombok.Getter;

@Getter
public enum EmailStateEnum {

    INNER_ERROR(-1001, "内部错误"), SUCCESS(1, "操作成功"), FAIL(0, "操作失败");

    private int state;
    private String stateInfo;

    private EmailStateEnum(int state, String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }
}
