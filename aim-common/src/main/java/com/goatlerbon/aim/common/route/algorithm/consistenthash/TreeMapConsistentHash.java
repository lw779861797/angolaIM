package com.goatlerbon.aim.common.route.algorithm.consistenthash;

import com.goatlerbon.aim.common.enums.StatusEnum;
import com.goatlerbon.aim.common.exception.AIMException;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 利用treemap 实现 一致性hash算法的核心思想是：
 * 找出 大于key的所有结点 ，并找出这些结点中 最小的第一个结点 就是 需要的那个结点
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private TreeMap<Long,String> treeMap = new TreeMap<>();

//    虚拟结点的数量
    private static final int VIRTUAL_NODE_SIZE = 10;

    @Override
    public String process(List<String> values, String key) {
        treeMap.clear();
        return super.process(values, key);
    }

    @Override
    protected void add(long key, String value){
        for(int i = 0;i < VIRTUAL_NODE_SIZE;i++){
            Long hash = super.hash("var"+key+i);
            treeMap.put(hash,value);
        }
        treeMap.put(key,value);
    }

    @Override
    protected String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        System.out.println("value=" + value + " hash = " + hash);
        //得到所有小于该hash 值的 结点
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            //第一个 结点（最小的）就是 要找的服务器
            return last.get(last.firstKey());
        }
        if (treeMap.size() == 0){
            throw new AIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        return treeMap.firstEntry().getValue();
    }
}
