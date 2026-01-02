package com.zxy.rpc.tansmission.socket;

import com.zxy.rpc.dto.RpcReq;
import com.zxy.rpc.dto.RpcResp;
import com.zxy.rpc.factory.SingletonFactory;
import com.zxy.rpc.registry.ServiceDiscovery;
import com.zxy.rpc.registry.impl.ZkServiceDiscovery;
import com.zxy.rpc.tansmission.RpcClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author zxy
 * @date 2025/12/27 17:15
 **/
@Slf4j
@AllArgsConstructor
public class SocketRpcClient implements RpcClient {

    private ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    @Override
    public RpcResp<?> send(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        try (Socket socket = new Socket(address.getAddress(), address.getPort())) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(req);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object o = in.readObject();
            return (RpcResp<?>) o;
        } catch (Exception e) {
            log.error("SocketRpcClient send error: ", e);
        }
        return null;
    }
}
