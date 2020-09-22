package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.MsgHandle;
import com.goatlerbon.aim.common.data.construct.RingBufferWheel;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 延时指令消息处理
 */
@Service
public class DelayMsgCommand implements InnerCommand {

    @Autowired
    EchoService echoService;

    @Autowired
    MsgHandle msgHandle;

    @Autowired
    RingBufferWheel ringBufferWheel;

    @Override
    public void process(String msg) {
        //相当于没输入 消息 和 时间
        if (msg.split(" ").length <=2){
            echoService.echo("incorrect commond, :delay [msg] [delayTime]") ;
            return ;
        }

//        取出时间
        String message = msg.split(" ")[1] ;
        Integer delayTime = Integer.valueOf(msg.split(" ")[2]);

        RingBufferWheel.Task task = new DelayMsgJob(message);
        task.setKey(delayTime);
        ringBufferWheel.addTask(task);

        echoService.echo(EmojiParser.parseToUnicode(message));
    }

    private class DelayMsgJob extends RingBufferWheel.Task{
        private String msg;

        public DelayMsgJob(String msg){
            this.msg = msg;
        }

        @Override
        public void run() {
            msgHandle.sendMsg(msg);
        }
    }
}
