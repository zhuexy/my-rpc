package com.zxy.rpc.constant;

/**
 * @Author zxy
 * @Date 2026/1/1 16:32
 **/
public interface RpcConst {
    public static final int SERVER_PORT = 8080;

    public static final String ZK_IP = "127.0.0.1";

    public static final int ZK_PORT = 2181;

    public static final String ZK_RPC_ROOT_PATH = "/my-rpc";

    public static final byte[] RPC_MAGIC_CODE = new byte[]{(byte) 'm', (byte) 'r', (byte) 'p', (byte) 'c'};

    public static final int RPC_REQ_HEAD_LEN = 16;
    public static final int RPC_REQ_MAX_LEN = 1024 * 1024 * 8;
}
