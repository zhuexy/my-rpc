package com.zxy.rpc.compress;

/**
 * @author zxy
 * @date 2026/1/11 16:01
 **/
public interface Compress {
    // 压缩
    byte[] compress(byte[] bytes);

    // 解压
    byte[] decompress(byte[] bytes);
}
