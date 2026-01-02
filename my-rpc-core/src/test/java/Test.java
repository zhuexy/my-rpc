import cn.hutool.core.net.NetUtil;

import java.net.InetSocketAddress;

/**
 * @author zxy
 * @date 2026/1/2 0:32
 **/
public class Test {
    @org.junit.Test
    public void test() {
        String hostAddress = NetUtil.getLocalhost().getHostAddress();
        System.out.println(hostAddress);
        System.out.println(NetUtil.getLocalhostStr());
        System.out.println(NetUtil.getLocalhost().getHostName());
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8080);
        System.out.println(inetSocketAddress.getHostName());
        System.out.println(inetSocketAddress.getHostString());
    }
}
