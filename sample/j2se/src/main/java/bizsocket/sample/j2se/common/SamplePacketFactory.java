package bizsocket.sample.j2se.common;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import okio.BufferedSource;
import okio.ByteString;
import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
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
        return SamplePacket.build(source);
    }
}
