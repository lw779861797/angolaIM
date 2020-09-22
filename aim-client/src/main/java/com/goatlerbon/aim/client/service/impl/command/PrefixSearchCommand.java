package com.goatlerbon.aim.client.service.impl.command;

import com.goatlerbon.aim.client.service.EchoService;
import com.goatlerbon.aim.client.service.InnerCommand;
import com.goatlerbon.aim.client.service.RouteRequest;
import com.goatlerbon.aim.client.vo.res.OnlineUsersResVo;
import com.goatlerbon.aim.common.data.construct.TrieTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前缀匹配用户指令 实现 基于字典树
 */
@Service
public class PrefixSearchCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrefixSearchCommand.class);

    @Autowired
    private RouteRequest routeRequest;

    @Autowired
    private EchoService echoService;
    @Override
    public void process(String msg) {
        try {
            List<OnlineUsersResVo.DataBodyBean> onlineUsers = routeRequest.onlineUsers();
            //字典树做模糊查询
            TrieTree trieTree = new TrieTree();
            for(OnlineUsersResVo.DataBodyBean onlineUser : onlineUsers){
                trieTree.insert(onlineUser.getUserName());
            }
            String[] split = msg.split(" ");
            String key = split[1];
            //模糊查询
            List<String > list = trieTree.prefixSearch(key);
            for (String res : list) {
                res = res.replace(key, "\033[31;4m" + key + "\033[0m");
                echoService.echo(res) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
