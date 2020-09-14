package com.goatlerbon.aim.client.service;

import com.goatlerbon.aim.client.enums.SystemCommandEnum;
import com.goatlerbon.aim.client.service.impl.command.PrintAllCommand;
import com.goatlerbon.aim.client.util.SpringBeanFactory;
import com.goatlerbon.aim.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InnerCommandContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(InnerCommandContext.class);

    /**
     * 获取执行器实例
     * @param command 执行器实例
     * @return
     */
    public InnerCommand getInstance(String command) {
        Map<String,String > allClazz = SystemCommandEnum.getAllClazz();

        //兼容需要命令后接参数的数据 :q cross
        String[] trim = command.trim().split(" ");
        String clazz = allClazz.get(trim[0]);
        InnerCommand innerCommand = null;
        try{
            //如果用户输入的指令不存在 则使用默认的指令处理器
            if(StringUtil.isEmpty(clazz)){
                clazz = PrintAllCommand.class.getName();
            }
            innerCommand = (InnerCommand) SpringBeanFactory.getBean(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            LOGGER.error("Exception", e);
        }
        return innerCommand;
    }
}
