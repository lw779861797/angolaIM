package com.goatlerbon.aim.route.kit;

import com.alibaba.fastjson.JSON;
import com.goatlerbon.aim.route.cache.ServerCache;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZKit {
    private static Logger logger = LoggerFactory.getLogger(ZKit.class);

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ServerCache serverCache ;

    /**
     * 监听 ZK中 父节点下面所有的子节点是否发生变化
     * @param path
     */
    public void subscribeEvent(String path) {
        //监听子节点是否发生变化
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                logger.info("Clear and update local cache parentPath=[{}],currentChildren=[{}]", parentPath,currentChildren.toString());

//                更新关于ZK结点的缓存 删除之前保存的ZK结点，并保存新的ZK结点currentChildren
                serverCache.updateCache(currentChildren);
            }
        });
    }

    /**
     * 获得 ZK中所有的服务器结点
     * @return
     */
    public List<String> getAllNode() {
        List<String> children = zkClient.getChildren("/route");
        logger.info("Query all node =[{}] success.", JSON.toJSONString(children));
        return children;
    }
}
