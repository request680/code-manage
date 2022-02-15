package com.example.filemanager.enums;

/**
 * @author mr.liu
 * @projectName file-manager
 * @package_name com.example.filemanager
 * @className ResponseStatusEnum
 * @description 响应状态枚举
 * @date 2022/2/13 14:28
 */
public enum ResponseStatusEnum {

    CODE200("200", "成功"),
    CODE400("400", "程序异常"),
    CODE500("500", "服务器异常");

    private String code;
    private String message;

    ResponseStatusEnum(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
