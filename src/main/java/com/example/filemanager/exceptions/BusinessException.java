package com.example.filemanager.exceptions;

import com.example.filemanager.enums.ResponseStatusEnum;

/**
 * @author mr.liu
 * @projectName file-manager
 * @package_name com.example.filemanager.exceptions
 * @className BusinessException
 * @description 业务异常
 * @date 2022/2/13 14:27
 */
public class BusinessException extends RuntimeException {

    String errorCode;
    String errorMessage;

    public BusinessException(String errorCode, String errorMessage){
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(String message){
        super(message);
        this.errorMessage = message;
    }

    public BusinessException(ResponseStatusEnum statusEnum){
        super(statusEnum.getMessage());
        this.errorCode = statusEnum.getCode();
        this.errorMessage = statusEnum.getMessage();
    }

    public BusinessException(Exception oriEx){
        super(oriEx);
    }

    public BusinessException(String message, Exception oriEx){
        super(message, oriEx);
        this.errorMessage = message;
    }

    public BusinessException(String message, Throwable cause){
        super(message, cause);
    }

    public BusinessException(Throwable cause){
        super(cause);
    }
}
