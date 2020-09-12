package com.goatlerbon.aim.common.exception;

import com.goatlerbon.aim.common.enums.StatusEnum;

public class AIMException extends GenericException {
    public AIMException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AIMException(Exception e, String errorCode, String errorMessage) {
        super(e, errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AIMException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public AIMException(StatusEnum statusEnum) {
        super(statusEnum.getMessage());
        this.errorMessage = statusEnum.message();
        this.errorCode = statusEnum.getCode();
    }

    public AIMException(StatusEnum statusEnum, String message) {
        super(message);
        this.errorMessage = message;
        this.errorCode = statusEnum.getCode();
    }

    public AIMException(Exception oriEx) {
        super(oriEx);
    }

    public AIMException(Throwable oriEx) {
        super(oriEx);
    }

    public AIMException(String message, Exception oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    public AIMException(String message, Throwable oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    /**
     * 判断是不是重置指令
     * @param msg
     * @return
     */
    public static boolean isResetByPeer(String msg) {
        if ("Connection reset by peer".equals(msg)) {
            return true;
        }
        return false;
    }
}
