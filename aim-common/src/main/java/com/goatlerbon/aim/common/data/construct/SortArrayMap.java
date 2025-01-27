package com.goatlerbon.aim.common.data.construct;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

public class SortArrayMap {

    /**
     * 核心数组
     */
    private Node[] buckets;

    /**
     * 默认大小
     */
    private static final int DEFAULT_SIZE = 32;

    /**
     * 数组大小
     */
    private int size = 0;

    public SortArrayMap() {
        buckets = new Node[DEFAULT_SIZE];
    }

    /**
     * 写入数据
     * @param key
     * @param value
     */
    public void add(Long key,String value){
        checkSize(size + 1);
        Node node = new Node(key,value);
        buckets[size++] = node;
    }

    /**
     * 监测是否需要扩容
     * @param size
     */
    public void checkSize(int size) {
        if(size >= buckets.length){
            //扩容自身的 3/2
            int oldLen = buckets.length;
            int newLen = oldLen + (oldLen >> 1);
            buckets = Arrays.copyOf(buckets,newLen);
        }
    }

    /**
     * 顺时针取出数据
     * @param key
     * @return
     */
    public String firstNodeValue(long key) {
        if (size == 0){
            return null ;
        }
        for (Node bucket : buckets) {
            if (bucket == null){
                break;
            }
            if (bucket.key >= key) {
                return bucket.value;
            }
        }

        return buckets[0].value;

    }

    public void sort(){
        Arrays.sort(buckets, 0, size, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.key > o2.key) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    public void print() {
        for (Node bucket : buckets) {
            if (bucket == null) {
                continue;
            }
            System.out.println(bucket.toString());
        }
    }

    public int size() {
        return size;
    }

    public void clear(){
        buckets = new Node[DEFAULT_SIZE];
        size = 0 ;
    }

    /**
     * 数据节点
     */
    private class Node{
        public Long key;
        public String value;

        public Node(Long key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
