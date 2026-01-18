import cn.hutool.core.net.NetUtil;
import com.zxy.rpc.serialize.Serializer;
import com.zxy.rpc.serialize.impl.KryoSerializer;
import com.zxy.rpc.spi.CustomLoader;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * @author zxy
 * @date 2026/1/2 0:32
 **/
public class RpcTest {
    @Test
    public void test() {
        String hostAddress = NetUtil.getLocalhost().getHostAddress();
        System.out.println(hostAddress);
        System.out.println(NetUtil.getLocalhostStr());
        System.out.println(NetUtil.getLocalhost().getHostName());
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8080);
        System.out.println(inetSocketAddress.getHostName());
        System.out.println(inetSocketAddress.getHostString());
    }

    @Test
    public void testSpi() {
        CustomLoader<Serializer> loader = CustomLoader.getLoader(Serializer.class);
        Serializer serializer = loader.get("kryo");
        System.out.println(serializer);
        assert serializer.getClass().equals(KryoSerializer.class);
    }
}
