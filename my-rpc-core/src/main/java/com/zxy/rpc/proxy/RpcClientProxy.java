package com.zxy.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.zxy.rpc.config.RpcServiceConfig;
import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.enums.RpcRespStatus;
import com.zxy.rpc.exception.RpcException;
import com.zxy.rpc.tansmission.RpcClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author zxy
 * @date 2025/12/28 19:37
 **/
@Slf4j
@AllArgsConstructor
public class RpcClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;

    private final RpcServiceConfig config;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig());
    }

    /**
     * 创建代理对象
     *
     * @param clazz 代理的对象class
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this);
    }

    /**
     * 代理方法
     * 代理对象执行方法时会调用此方法
     *
     * @param proxy  代理对象
     * @param method 代理方法
     * @param args   方法参数
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcReq rpcReq = RpcReq.builder()
                .requestId(IdUtil.fastSimpleUUID())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .params(args)
                .version(config.getVersion())
                .group(config.getGroup())
                .build();
        RpcResp<?> resp = rpcClient.send(rpcReq);

        check(rpcReq, resp);
        return resp.getData();
    }

    private void check(RpcReq rpcReq, RpcResp<?> rpcResp) {
        if (rpcResp == null) {
            throw new RpcException("rpcResp为空");
        }
        if (!Objects.equals(rpcResp.getRequestId(), rpcReq.getRequestId())) {
            throw new RpcException("请求ID不一致");
        }
        if (!Objects.equals(rpcResp.getCode(), RpcRespStatus.SUCCESS.getCode())) {
            throw new RpcException("响应值为失败" + rpcResp.getMsg());
        }
    }
}
