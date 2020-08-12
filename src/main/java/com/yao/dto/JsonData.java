package com.yao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonData {

    private boolean ret;
    private String msg;
    private Object data;

    public JsonData(boolean ret){
        this.ret = ret;
    }

    public static JsonData success(Object data, String msg){
        JsonData jsonData = new JsonData(true);
        jsonData.setData(data);
        jsonData.setMsg(msg);
        return jsonData;
    }
    public static JsonData success(Object data){
        return success(data, null);
    }
    public static JsonData success(){
        return success(null, null);
    }

    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.setMsg(msg);
        return jsonData;
    }
    public static JsonData fail(){
        return fail(null);
    }
}
