package com.zxy.client;

import com.zxy.rpc.annotation.RpcReferenceScan;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.util.ProxyUtils;

@RpcReferenceScan("com.zxy.client")
public class Main {

    public static void main(String[] args) throws InterruptedException {
        ProxyUtils.scanReference(Main.class);
        TestController testController = SingletonFactory.getInstance(TestController.class);
        testController.test();
    }
}