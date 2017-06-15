package bizsocket.core;

import bizsocket.core.cache.*;
import bizsocket.tcp.Request;
import client.SamplePacketFactory;
import bizsocket.tcp.Packet;
import client.SampleSocketClient;
import common.SampleBizPacketValidator;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

/**
 * Created by tong on 16/10/5.
 */
public class CacheManagerTest extends TestCase {
    CacheManager cacheManager;

    private PacketValidator validator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        cacheManager = new CacheManager(new SampleSocketClient(new Configuration()));
        validator = new SampleBizPacketValidator();
        System.out.println("setUp");
    }

    @Test
    public void testPersistenceCacheEntry() throws Exception {
        System.out.println("testPersistenceCacheEntry");

        int command = 1;
        CacheStrategy cacheStrategy = new CacheStrategy(command, validator);
        assertNull(cacheManager.get(command));
        cacheManager.add(cacheStrategy);
        assertNotNull(cacheManager.get(command));

        Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(22).utf8body(new JSONObject().toString()).build());
        try {
            cacheStrategy.updateCache(packet);
            fail("入口的command与被缓存的包命令号不一致");
        } catch (IllegalArgumentException e) {

        }

        assertNull(cacheStrategy.getValidCache());

        JSONObject response = new JSONObject();
        response.put("code",-1);
        packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());
        cacheStrategy.updateCache(packet);
        assertNull(cacheStrategy.getValidCache());

        cacheStrategy.setValidator(null);

        response = new JSONObject();
        response.put("code",-1);
        packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());
        cacheStrategy.updateCache(packet);
        assertNotNull(cacheStrategy.getValidCache());
    }

    @Test
    public void testCounterCacheStrategy() throws Exception {
        System.out.println("testRelativeMillisCacheEntry");

        final int ecount = 10;
        int command = 1;
        CounterCacheStrategy cacheStrategy = new CounterCacheStrategy(command, ecount,validator);
        cacheManager.add(cacheStrategy);

        JSONObject response = new JSONObject();
        response.put("code",200);
        Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());


        for (int j = 0; j < 2; j++) {
            cacheStrategy.updateCache(packet);

            for (int i = 0; i <= ecount; i++) {
                if (i < ecount) {
                    assertNotNull(cacheStrategy.getValidCache());
                }
                else {
                    assertNull(cacheStrategy.getValidCache());
                }
                cacheStrategy.onHit();

                System.out.println("expiresCount: " + cacheStrategy.getExpiresCount() + " current: " + cacheStrategy.getCount());
            }
        }
    }

    @Test
    public void testRelativeMillisCacheStrategy() throws Exception {
        System.out.println("testRelativeMillisCacheStrategy");

        int command = 1;
        CacheStrategy cacheStrategy = new RelativeMillisCacheStrategy(command, TimeUnit.SECONDS, 2,validator);
        cacheManager.add(cacheStrategy);

        JSONObject response = new JSONObject();
        response.put("code",200);
        Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());
        cacheStrategy.updateCache(packet);
        assertNotNull(cacheStrategy.getValidCache());
        Thread.sleep(2500);
        assertNull(cacheStrategy.getValidCache());
    }

    @Test
    public void testUseUtilSendCmdCacheStrategy() throws Exception {
        System.out.println("testUseUtilSendCmdCacheStrategy");

        int command = 1;
        int[] conflictCommands = new int[]{2,3,4};
        UseUtilSendCmdCacheStrategy cacheStrategy = new UseUtilSendCmdCacheStrategy(command, conflictCommands,validator);
        cacheManager.add(cacheStrategy);

        for (int conflictCmd : conflictCommands) {
            JSONObject response = new JSONObject();
            response.put("code",200);
            Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());
            cacheStrategy.updateCache(packet);
            assertNotNull(cacheStrategy.getValidCache());

            packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(conflictCmd).utf8body(response.toString()).build());
            cacheStrategy.processTriggerPacket(packet);
            assertNull(cacheStrategy.getValidCache());
        }

        PacketValidator packetValidator = new PacketValidator() {
            @Override
            public boolean verify(Packet packet) {
                return false;
            }
        };
        cacheStrategy.setTriggerPacketValidator(packetValidator);
        for (int conflictCmd : conflictCommands) {
            JSONObject response = new JSONObject();
            response.put("code",200);
            Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());
            cacheStrategy.updateCache(packet);
            assertNotNull(cacheStrategy.getValidCache());

            packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(conflictCmd).utf8body(response.toString()).build());
            cacheStrategy.processTriggerPacket(packet);
            assertNotNull(cacheStrategy.getValidCache());
        }
    }

    @Test
    public void testUseUtilReceiveCmdCacheStrategy() throws Exception {
        System.out.println("testUseUtilReceiveCmdCacheStrategy");

        int command = 1;
        int[] conflictCommands = new int[]{2,3,4};
        UseUtilReceiveCmdCacheStrategy cacheStrategy = new UseUtilReceiveCmdCacheStrategy(command, conflictCommands,validator);
        cacheManager.add(cacheStrategy);

        JSONObject response = new JSONObject();
        response.put("code",200);
        Packet packet = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(command).utf8body(response.toString()).build());

        for (int conflictCmd : conflictCommands) {
            cacheStrategy.updateCache(packet);
            assertNotNull(cacheStrategy.getValidCache());

            Packet triggerPacket = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(conflictCmd).utf8body(response.toString()).build());
            cacheStrategy.processTriggerPacket(triggerPacket);
            assertNull(cacheStrategy.getValidCache());
        }

        cacheStrategy.setTriggerPacketValidator(validator);
        for (int conflictCmd : conflictCommands) {
            response.put("code",200);
            cacheStrategy.updateCache(packet);
            assertNotNull(cacheStrategy.getValidCache());

            response.put("code",-1);
            Packet triggerPacket = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(conflictCmd).utf8body(response.toString()).build());
            cacheStrategy.processTriggerPacket(triggerPacket);
            assertNotNull(cacheStrategy.getValidCache());

            response.put("code",200);
            triggerPacket = new SamplePacketFactory().getRequestPacket(new Request.Builder().command(conflictCmd).utf8body(response.toString()).build());
            cacheStrategy.processTriggerPacket(triggerPacket);
            assertNull(cacheStrategy.getValidCache());
        }
    }
}
