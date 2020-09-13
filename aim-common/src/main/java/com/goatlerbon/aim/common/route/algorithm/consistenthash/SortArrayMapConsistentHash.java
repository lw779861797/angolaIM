package com.goatlerbon.aim.common.route.algorithm.consistenthash;

import com.goatlerbon.aim.common.data.construct.SortArrayMap;

import java.util.List;

/**
 * 自定义排序 Map 实现
 */
public class SortArrayMapConsistentHash extends AbstractConsistentHash {

    private SortArrayMap sortArrayMap = new SortArrayMap();

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_SIZE = 10 ;

    @Override
    public String process(List<String> values, String key) {
        sortArrayMap.clear();
        return super.process(values, key);
    }

    @Override
    protected void add(long key, String value) {
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            Long hash = super.hash("vir" + key + i);
            sortArrayMap.add(hash,value);
        }
        sortArrayMap.add(key, value);
    }

    @Override
    protected String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        System.out.println("value=" + value + " hash = " + hash);
        return sortArrayMap.firstNodeValue(hash);
    }

    @Override
    protected void sort() {
        sortArrayMap.sort();
    }
}
