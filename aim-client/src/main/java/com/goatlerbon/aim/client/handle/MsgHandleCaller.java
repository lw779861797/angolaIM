package com.goatlerbon.aim.client.handle;

import com.goatlerbon.aim.client.service.CustomMsgHandleListener;

/**
 * 消息回调Bean
 */
public class MsgHandleCaller {

    /**
     * 回调接口
     */
    private CustomMsgHandleListener msgHandleListener;

    public MsgHandleCaller(CustomMsgHandleListener msgHandleListener) {
        this.msgHandleListener = msgHandleListener;
    }

    public CustomMsgHandleListener getMsgHandleListener() {
        return msgHandleListener;
    }

    public void setMsgHandleListener(CustomMsgHandleListener msgHandleListener) {
        this.msgHandleListener = msgHandleListener;
    }
}
