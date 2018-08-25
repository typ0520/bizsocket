package bizsocket.sample.j2se;

import bizsocket.core.*;
import bizsocket.base.JSONRequestConverter;
import bizsocket.base.JSONResponseConverter;
import bizsocket.rx2.BizSocketRxSupport;
import bizsocket.sample.j2se.common.*;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okio.ByteString;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

/**
 * Created by tong on 16/10/3.
 */
public class SampleClient extends AbstractBizSocket {
    public SampleClient(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return new SamplePacketFactory();
    }

    public static void main(String[] args) {
        SampleClient client = new SampleClient(new Configuration.Builder()
                .host("127.0.0.1")
                .port(9103)
                .readTimeout(TimeUnit.SECONDS,30)
                .heartbeat(60)
                .build());

        //增加串行数据的处理(把两个命令返回的数据进行合并)
        client.addSerialSignal(new SerialSignal(OrderListSerialContext.class, SampleCmd.QUERY_ORDER_LIST.getValue(),
                new int[]{SampleCmd.QUERY_ORDER_LIST.getValue(), SampleCmd.QUERY_ORDER_TYPE.getValue()}));

        //如果需要把
        client.getOne2ManyNotifyRouter().addStickyCmd(SampleCmd.NOTIFY_PRICE.getValue(),new SampleBizPacketValidator());

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

        //创建rxjava请求环境(类似于retrofit)
        BizSocketRxSupport rxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(client)
                .build();
        SampleService service = rxSupport.create(SampleService.class);

        try {
            //连接
            client.connect();
            //启动断线重连
            client.getSocketConnection().bindReconnectionManager();
            //开启心跳
            client.getSocketConnection().startHeartBeat();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //注册通知
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

        //typ0520 ks im
        //{"address":"4078b6269117f95bc03b18e00617b005ab233b2d","crypto":{"cipher":"aes-128-ctr","cipherparams":{"iv":"068c628e6860cf13803a74eb18d4dae3"},"ciphertext":"2c76e25ab190b247f7318c4d8f184ca76ea56c3b0c60997b3ead1fbbd0d44d7f","kdf":"scrypt","kdfparams":{"dklen":32,"n":65536,"p":1,"r":8,"salt":"27234c1bd39af1f9252faac96025ab66cad1e0f22ba0277373457849060003c6"},"mac":"84e100908c6ff41a92df5235080a9ed034e0a5d0851f40030d2f9d2732fe1ea3"},"id":"08a8d514-35e8-4fe3-b3bb-9223a6a014c6","version":3}
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


        JSONObject params = new JSONObject();
        try {
            params.put("pageSize","10000");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.queryOrderList(params).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                System.out.println("rx response: " + jsonObject);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

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
