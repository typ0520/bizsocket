package bizsocket.base;

import bizsocket.core.AbstractBizSocket;
import bizsocket.core.Configuration;
import bizsocket.core.ResponseHandler;
import bizsocket.tcp.PacketFactory;
import client.SamplePacketFactory;
import junit.framework.TestCase;
import okio.ByteString;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import java.lang.reflect.Method;

/**
 * Created by tong on 16/10/8.
 */
public class BizSocketCallTest extends TestCase {
    public interface TestService1 {
        @Request(cmd = 1)
        Observable<String> testGetTag1(Object tag
                ,@Query("username") String username,
                                   @Query("password") String password);

        @Request(cmd = 1)
        Observable<String> testGetTag2(@Tag Object tag
                ,@Query("username") String username,
                                   @Query("password") String password);

        Observable<String> testCall1(String username,String password);

        @Request(cmd = 22)
        Observable<String> testCall2(String username,String password);

        @Request(cmd = 22)
        Observable<String> testCall3(@Tag Object tag,String username,String password);

        @Request(cmd = 22)
        Observable<String> testCall4(@Tag Object tag,@Tag Object tag2,String username,String password);

        @Request(cmd = 22)
        Observable<String> testCall5(@Tag Object tag,String username,String password);

        @Request(cmd = 22)
        Observable<JSONObject> testCall6(@Tag Object tag,String username,String password);

        @Request(cmd = 22)
        Observable<CodeMsg> testCall7(@Tag Object tag,String username,String password);

        @Request(cmd = 22)
        Observable testCall8(@Tag Object tag,String username,String password);

        @Request(cmd = 22)
        Observable testCall9(@Cmd int cmd, @Query("username") String username);
    }

    public static class CodeMsg {
        public int code;
        public String msg;

        @Override
        public String toString() {
            return "CodeMsg{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }

    private Class<?> serviceClazz = TestService1.class;

    @Test
    public void testGetTag1() throws Exception {
        BizSocketCall call = new BizSocketCall();
        Method method = serviceClazz.getMethod("testGetTag1",Object.class,String.class,String.class);

        Object tag = new Object();
        Object returnTag = call.getTag(method, tag, "", "");
        assertNotNull(returnTag);
    }

    @Test
    public void testGetTag2() throws Exception {
        BizSocketCall call = new BizSocketCall();
        Method method = serviceClazz.getMethod("testGetTag2",Object.class,String.class,String.class);

        Object tag = new Object();
        Object returnTag = call.getTag(method, tag, "", "");
        assertNotNull(returnTag);
        assertEquals(tag,returnTag);
    }

    @Test
    public void testCall1() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return null;
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall1",String.class,String.class);
        BizSocketCall call = new BizSocketCall();
        try {
            call.call(bizSocketRxSupport,method,"username","password");
            fail("必须加上Request注解");
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testCall2() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return null;
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall2",String.class,String.class);
        BizSocketCall call = new BizSocketCall();
        //不写tag tag设置成call
        call.call(bizSocketRxSupport,method,"username","password");
    }

    @Test
    public void testCall3() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return null;
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall3",Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        call.call(bizSocketRxSupport,method,new Object(),"username","password");
    }

    @Test
    public void testCall4() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return null;
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall4",Object.class,Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        //tag允许重复，以前面的为准
        call.call(bizSocketRxSupport,method,new Object(),new Object(),"username","password");
    }

    @Test
    public void testCall5() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return new SamplePacketFactory();
            }


            @Override
            public Object request(bizsocket.tcp.Request request, ResponseHandler responseHandler) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code",200);
                    obj.put("msg","ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteString responseStr = ByteString.encodeUtf8(obj.toString());

                responseHandler.sendSuccessMessage(22, ByteString.encodeUtf8("{}"), getPacketFactory().getRequestPacket(new bizsocket.tcp.Request.Builder().command(22).body(responseStr).build()));
                return new Object();
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall5",Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        Observable<String> observable = (Observable<String>) call.call(bizSocketRxSupport, method, new Object(), "myusername", "mypassword");
        observable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail(e.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        });
    }

    @Test
    public void testCall6() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return new SamplePacketFactory();
            }

            @Override
            public Object request(bizsocket.tcp.Request request, ResponseHandler responseHandler) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code",200);
                    obj.put("msg","ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteString responseStr = ByteString.encodeUtf8(obj.toString());

                responseHandler.sendSuccessMessage(22, ByteString.encodeUtf8("{}"), getPacketFactory().getRequestPacket(new bizsocket.tcp.Request.Builder().command(22).body(responseStr).build()));
                return new Object();
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall6",Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        Observable<JSONObject> observable = (Observable<JSONObject>) call.call(bizSocketRxSupport, method, new Object(), "myusername", "mypassword");
        observable.subscribe(new Subscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail(e.getMessage());
            }

            @Override
            public void onNext(JSONObject s) {
                System.out.println(s);
            }
        });
    }

    @Test
    public void testCall7() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return new SamplePacketFactory();
            }

            @Override
            public Object request(bizsocket.tcp.Request request, ResponseHandler responseHandler) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code",200);
                    obj.put("msg","ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteString responseStr = ByteString.encodeUtf8(obj.toString());

                responseHandler.sendSuccessMessage(22, ByteString.encodeUtf8("{}"), getPacketFactory().getRequestPacket(new bizsocket.tcp.Request.Builder().command(22).body(responseStr).build()));

                return new Object();
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall7",Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        Observable<CodeMsg> observable = (Observable<CodeMsg>) call.call(bizSocketRxSupport, method, new Object(), "myusername", "mypassword");
        observable.subscribe(new Subscriber<CodeMsg>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail(e.getMessage());
            }

            @Override
            public void onNext(CodeMsg s) {
                System.out.println(s);
            }
        });
    }

    @Test
    public void testCall8() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return new SamplePacketFactory();
            }

            @Override
            public Object request(bizsocket.tcp.Request request, ResponseHandler responseHandler) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code",200);
                    obj.put("msg","ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteString responseStr = ByteString.encodeUtf8(obj.toString());

                responseHandler.sendSuccessMessage(22, ByteString.encodeUtf8("{}"), getPacketFactory().getRequestPacket(new bizsocket.tcp.Request.Builder().command(22).body(responseStr).build()));
                return new Object();
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall8",Object.class,String.class,String.class);
        BizSocketCall call = new BizSocketCall();

        Observable observable = call.call(bizSocketRxSupport, method, new Object(), "myusername", "mypassword");
        observable.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object s) {
                System.out.print(s);
            }
        });
    }

    @Test
    public void testCall9() throws Exception {
        class TestBizSocket extends AbstractBizSocket {
            public TestBizSocket(Configuration configuration) {
                super(configuration);
            }

            @Override
            protected PacketFactory createPacketFactory() {
                return new SamplePacketFactory();
            }

            @Override
            public Object request(bizsocket.tcp.Request request, ResponseHandler responseHandler) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("code",200);
                    obj.put("msg","ok");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ByteString responseStr = ByteString.encodeUtf8(obj.toString());

                responseHandler.sendSuccessMessage(22, ByteString.encodeUtf8("{}"), getPacketFactory().getRequestPacket(new bizsocket.tcp.Request.Builder().command(22).body(responseStr).build()));
                return new Object();
            }
        }

        TestBizSocket bizSocket = new TestBizSocket(new Configuration.Builder()
                .host("127.0.0.1")
                .port(8080).build());
        BizSocketRxSupport bizSocketRxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(bizSocket).build();


        Method method = serviceClazz.getMethod("testCall9",int.class,String.class);
        BizSocketCall call = new BizSocketCall();

        Observable observable = call.call(bizSocketRxSupport, method, 88,"ss");
        observable.subscribe(new Subscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object s) {

            }
        });
    }
}
