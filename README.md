# bizsocket

[![license](https://img.shields.io/hexpm/l/plug.svg)](https://raw.githubusercontent.com/baidao/bizsocket/master/LICENSE)

[ ![Download](https://api.bintray.com/packages/typ0520/maven/com.github.typ0520%3Abizsocket-core/images/download.svg) ](https://bintray.com/typ0520/maven/com.github.typ0520%3Abizsocket-core/_latestVersion)

## 异步socket，对一些业务场景做了支持

- 断线重连
- 一对一请求
- 通知、粘性通知
- 串行请求合并
- 包分片处理(AbstractFragmentRequestQueue)
- 缓存
- 拦截器
- 支持rxjava，提供类似于retrofit的支持

## 使用方式

Maven

```
<dependency>
  <groupId>com.github.typ0520</groupId>
  <artifactId>bizsocket-rx</artifactId>
  <version>1.0.0</version>
</dependency>
```

or Gradle

```gradle
buildscript {
	repositories {
	   jcenter()
	}
}
 
dependencies {
	compile 'com.github.typ0520:bizsocket-rx:1.0.0'
}
```

## 适用协议

如果想使用此库，客户端和服务器的通讯协议中必须要有命令号、包序列号这两个字段

- 命令号代表请求类型，可以想象成http中url的作用
- 包序列号是数据包的唯一索引，客户端发起请求时为数据包生成一个唯一索引，服务器返回请求对应的结果时把这个包序列号带回去

协议可以类似于下面这种：

| cmd        | packetId | contentLength |content|
| -----------|:-------------:| ---------------------------:| ---------------------------:|
|int         |int            |           int            |           byte[]            |

也可以类似于下面这样的每个数据包都是一段json字符串，包与包之间用换行符分割

```json
{"cmd": xxx , "packetId": xxx , ...... } 
```

数据包的创建是通过这两个抽象类PacketFactory、Packet，整个库的数据流转都是通过命令号、包序列号这两个字段来完成的，字段名、出现的位置以及形式不限，只要有这两个字段就适用此库

## 配置BizSocket

sample中client与server之间的通讯协议是

| length(int)  | cmd(int) |   seq(int)        |   content(byte[])               |
| -------------|:--------:| -----------------:| -------------------------------:|
| 数据包的总长度 |  命令号   |  数据包的唯一索引     | 报文体，可以想象成http协议中的body |

下面的代码片段来自[sample](https://github.com/typ0520/bizsocket/sample/j2se)，建议把代码拉下来看


- 1、 首先需要创建一个数据包类继承自Packet

```java
public class SamplePacket extends Packet {
    static volatile int currentSeq = 0;
    public int length;
    public int cmd;
    public int seq;
    public String content;
    
    @Override
    public int getCommand() {
    	 //覆盖父类的抽象方法
        return cmd;
    }

    @Override
    public String getPacketID() {
        //覆盖父类的抽象方法
        return String.valueOf(seq);
    }
    
    //获取请求数据包byte[]，写给服务器
    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(bos));
        try {
            //包长 = 内容长度 + 包头固定的12个字节
            ByteString byteString = ByteString.encodeUtf8(content);
            bufferedSink.writeInt(byteString.size() + 12);
            bufferedSink.writeInt(cmd);
            bufferedSink.writeInt(seq);
            bufferedSink.write(byteString);
            bufferedSink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
}
```

- 2、创建PacketFactory，主要用来从流中解析出server发给client的数据包

```java
public class SamplePacketFactory extends PacketFactory {
    @Override
    public Packet getRequestPacket(Packet reusable,Request request) {
        return new SamplePacket(request.command(),request.body());
    }

    @Override
    public Packet getHeartBeatPacket(Packet reusable) {
        return new SamplePacket(SampleCmd.HEARTBEAT.getValue(), ByteString.encodeUtf8("{}"));
    }

    @Override
    public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
      	 SamplePacket packet = new SamplePacket();
        packet.length = reader.readInt();
        packet.cmd = reader.readInt();
        packet.seq = reader.readInt();
        //减去协议头的12个字节长度
        packet.content = reader.readString(packet.length - 12, Charset.forName("utf-8"));
        return packet;
    }
}
```

- 3、配置client

```java
public class SampleClient extends AbstractBizSocket {
    public SampleClient(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return new SamplePacketFactory();
    }
}
```

- 3、启动client，以j2se为例写一个main方法

```java
public static void main(String[] args) {
        SampleClient client = new SampleClient(new Configuration.Builder()
                .host("127.0.0.1")
                .port(9103)
                .readTimeout(TimeUnit.SECONDS,30)
                .heartbeat(60)
                .build());

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
            //连接
            client.connect();
            //启动断线重连
            client.getSocketConnection().bindReconnectionManager();
            //开启心跳
            client.getSocketConnection().startHeartBeat();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //注册通知，接收服务端的推送
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

	//发起一对一请求
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

	//如果想用rxjava的形式调用也是支持的,提供了类似于retrofit通过动态代理创建的service类来调用
        BizSocketRxSupport rxSupport = new BizSocketRxSupport.Builder()
                .requestConverter(new JSONRequestConverter())
                .responseConverter(new JSONResponseConverter())
                .bizSocket(client)
                .build();
        SampleService service = rxSupport.create(SampleService.class);

        JSONObject params = new JSONObject();
        try {
            params.put("pageSize","10000");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.queryOrderList(params).subscribe(new Subscriber<JSONObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                System.out.println("rx response: " + jsonObject);
            }
        });

	//阻塞主线程，防止程序退出，可以想象成android中的Looper类
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```
