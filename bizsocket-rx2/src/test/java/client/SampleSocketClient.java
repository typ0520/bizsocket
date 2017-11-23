package client;

import bizsocket.core.*;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.SampleCmd;
import okio.ByteString;

/**
 * Created by tong on 16/10/3.
 */
public class SampleSocketClient extends AbstractBizSocket {
    public SampleSocketClient(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return new SamplePacketFactory();
    }

    public static void main(String[] args) {
        SampleSocketClient client = new SampleSocketClient(new Configuration.Builder()
                .host("127.0.0.1")
                .port(9103)
                .build());
        client.addSerialSignal(new SerialSignal(OrderListSerialContext.class, SampleCmd.QUERY_ORDER_LIST.getValue(),
                new int[]{SampleCmd.QUERY_ORDER_LIST.getValue(), SampleCmd.QUERY_ORDER_TYPE.getValue()}));

        client.getInterceptorChain().addInterceptor(new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                System.out.println("发现一个请求postRequestHandle: " + context);
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                System.out.println("收到一个包postResponseHandle: " + responsePacket);
                return false;
            }
        });
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.subscribe(client, SampleCmd.NOTIFY_PRICE.getValue(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";

        client.request(new Request.Builder().command(SampleCmd.CREATE_ORDER.getValue()).utf8body(json).build(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " attach: " + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        json = "{\"pageSize\" : \"10000\"}";
        client.request(new Request.Builder().command(SampleCmd.QUERY_ORDER_LIST.getValue()).utf8body(json).build(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
