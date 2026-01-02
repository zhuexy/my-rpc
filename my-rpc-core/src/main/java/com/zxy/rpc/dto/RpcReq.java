package com.zxy.rpc.dto;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zxy
 * @date 2025/12/27 16:32
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcReq implements Serializable {

    private static final long serialVersionUID = 1L;
    // 请求id
    private String requestId;
    // 要调用的接口名
    private String interfaceName;
    // 要调用的方法名
    private String methodName;
    // 方法参数类型
    private Class<?>[] paramTypes;
    // 方法参数
    private Object[] params;
    // 服务版本号
    private String version;
    // 服务分组
    private String group;

    public String rpcServiceName() {
        return this.interfaceName
                + StrUtil.blankToDefault(this.version, StrUtil.EMPTY)
                + StrUtil.blankToDefault(this.group, StrUtil.EMPTY);
    }
}
