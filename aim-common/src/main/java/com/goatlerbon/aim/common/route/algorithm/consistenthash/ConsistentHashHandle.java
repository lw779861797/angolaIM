package com.goatlerbon.aim.common.route.algorithm.consistenthash;

import com.goatlerbon.aim.common.route.algorithm.RouteHandle;
import jdk.vm.ci.meta.Value;

import java.util.List;

public class ConsistentHashHandle implements RouteHandle {

    private AbstractConsistentHash hash;

    public void setHash(AbstractConsistentHash hash){
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values,key);
    }
}
