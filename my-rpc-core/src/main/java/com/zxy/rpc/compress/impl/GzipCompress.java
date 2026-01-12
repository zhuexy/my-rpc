package com.zxy.rpc.compress.impl;

import cn.hutool.core.util.ZipUtil;
import com.zxy.rpc.compress.Compress;

/**
 * @author zxy
 * @date 2026/1/11 16:01
 **/
public class GzipCompress implements Compress {
    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.gzip(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.unGzip(bytes);
    }
}
