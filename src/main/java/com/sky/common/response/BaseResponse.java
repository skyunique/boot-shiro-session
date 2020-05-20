package com.sky.common.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
