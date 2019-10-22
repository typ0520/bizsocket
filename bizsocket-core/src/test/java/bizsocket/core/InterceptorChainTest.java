package bizsocket.core;

import bizsocket.tcp.Packet;
import common.SamplePacket;
import junit.framework.TestCase;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by tong on 16/10/6.
 */
public class InterceptorChainTest extends TestCase {
    private int receivePostRequestHandleCount;
    private int receivePostResponseHandleCount;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        receivePostRequestHandleCount = 0;
        receivePostResponseHandleCount = 0;
    }

    @Test
    public void testAddInterceptor() throws Exception {
        InterceptorChain chain = new InterceptorChain();

        Interceptor interceptor = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return false;
            }
        };
        chain.addInterceptor(interceptor);
        chain.addInterceptor(interceptor);

        chain.invokePostRequestHandle(new RequestContext(null,null,null));
        chain.invokePesponseHandle(1, new SamplePacket());

        assertEquals(receivePostRequestHandleCount,1);
        assertEquals(receivePostResponseHandleCount,1);
    }

    @Test
    public void testAddInterceptor2() throws Exception {
        InterceptorChain chain = new InterceptorChain();

        Interceptor interceptor = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return false;
            }
        };

        Interceptor interceptor2 = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return false;
            }
        };
        chain.addInterceptor(interceptor);
        chain.addInterceptor(interceptor2);

        chain.invokePostRequestHandle(new RequestContext(null,null,null));
        chain.invokePesponseHandle(1, new SamplePacket());

        assertEquals(receivePostRequestHandleCount,2);
        assertEquals(receivePostResponseHandleCount,2);
    }

    @Test
    public void testAddInterceptor3() throws Exception {
        InterceptorChain chain = new InterceptorChain();

        Interceptor interceptor = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return true;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return false;
            }
        };

        Interceptor interceptor2 = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return true;
            }
        };

        Interceptor interceptor3 = new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                receivePostRequestHandleCount += 1;
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                receivePostResponseHandleCount += 1;
                return false;
            }
        };
        chain.addInterceptor(interceptor);
        chain.addInterceptor(interceptor2);
        chain.addInterceptor(interceptor3);

        Field field = chain.getClass().getDeclaredField("interceptors");
        field.setAccessible(true);
        List<Interceptor> interceptors = (List<Interceptor>) field.get(chain);
        assertEquals(interceptors.size(),3);
        chain.invokePostRequestHandle(new RequestContext(null,null,null));
        chain.invokePesponseHandle(1, new SamplePacket());

        assertEquals(receivePostRequestHandleCount,1);
        assertEquals(receivePostResponseHandleCount,2);
    }
}
