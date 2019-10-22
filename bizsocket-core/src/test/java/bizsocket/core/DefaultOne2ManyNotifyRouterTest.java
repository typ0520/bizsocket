package bizsocket.core;

import bizsocket.tcp.Packet;
import common.SampleCmd;
import common.SamplePacket;
import common.SampleProtocolUtil;
import junit.framework.TestCase;
import okio.ByteString;
import org.junit.Test;

/**
 * Created by tong on 16/10/6.
 */
public class DefaultOne2ManyNotifyRouterTest extends TestCase {
    private One2ManyNotifyRouter router;
    SamplePacket receivePacket = null;

    private PacketValidator packetValidator = new PacketValidator() {
        @Override
        public boolean verify(Packet packet) {
            return SampleProtocolUtil.isSuccessResponsePacket((SamplePacket) packet);
        }
    };

    public DefaultOne2ManyNotifyRouterTest() {
        router = new DefaultOne2ManyNotifyRouter();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        receivePacket = null;
    }

    @Test
    public void testAddStickyCmd() throws Exception {
        router.addStickyCmd(SampleCmd.NOTIFY_PRICE.getValue(),packetValidator);
        SamplePacket packet = new SamplePacket(SampleCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));
        router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);

        router.subscribe(this, SampleCmd.NOTIFY_PRICE.getValue(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (SamplePacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });

        assertNull(receivePacket);

        packet = new SamplePacket(SampleCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{\"code\" : 200}"));
        router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
        assertNotNull(receivePacket);
    }

    @Test
    public void testRouteFLAG_DEFAULT() throws Exception{
        router.subscribe(this, SampleCmd.NOTIFY_PRICE.getValue(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (SamplePacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        });

        SamplePacket packet = new SamplePacket(SampleCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            receivePacket = null;
            router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
            assertEquals(packet,receivePacket);
        }
    }

    @Test
    public void testUnSubscribe() throws Exception{
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (SamplePacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        };
        router.subscribe(this, SampleCmd.NOTIFY_PRICE.getValue(), responseHandler);

        SamplePacket packet = new SamplePacket(SampleCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                receivePacket = null;
                router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
                assertEquals(packet,receivePacket);
                router.unsubscribe(responseHandler);

                receivePacket = null;
            }
            else {
                router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
                assertNull(receivePacket);
            }
        }
    }

    @Test
    public void testUnSubscribe2() throws Exception{
        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                receivePacket = (SamplePacket) responsePacket;
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {

            }
        };
        router.subscribe(this, SampleCmd.NOTIFY_PRICE.getValue(), responseHandler);

        SamplePacket packet = new SamplePacket(SampleCmd.NOTIFY_PRICE.getValue(),ByteString.encodeUtf8("{}"));

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                receivePacket = null;
                router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
                assertEquals(packet,receivePacket);
                router.unsubscribe(new Object());
                assertEquals(packet,receivePacket);
                router.unsubscribe(this);

                receivePacket = null;
            }
            else {
                router.route(SampleCmd.NOTIFY_PRICE.getValue(),packet);
                assertNull(receivePacket);
            }
        }
    }
}
