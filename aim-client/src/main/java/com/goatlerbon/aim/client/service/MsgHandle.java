package com.goatlerbon.aim.client.service;

/**
 * 消息处理器
 */
public interface MsgHandle {
    // TODO: 2018/12/26 后续对消息的处理可以优化为责任链模式
    /**
     * 校验消息
     * @param msg
     * @return 不能为空，后续可以加上一些敏感词
     * @throws Exception
     */
    boolean checkMsg(String msg);

    /**
     * 执行内部命令
     * @param msg
     * @return 是否应当跳过当前消息（包含了":" 就需要跳过）
     */
    boolean innerCommand(String msg);

    /**
     * 统一的发送接口
     * @param msg
     */
    void sendMsg(String msg);

    /**
     * 关闭系统
     */
    void shutdown();
}
