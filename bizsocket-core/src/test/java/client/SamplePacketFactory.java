package client;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.SampleCmd;
import common.SamplePacket;
import okio.BufferedSource;
import java.io.IOException;

/**
 * Created by tong on 16/10/5.
 */
public class SamplePacketFactory extends PacketFactory {
    @Override
    public Packet getRequestPacket(Packet reusable,Request request) {
        if (reusable != null && reusable instanceof SamplePacket) {
            SamplePacket packet = (SamplePacket) reusable;
            packet.setCommand(request.command());
            packet.setContent(request.body().utf8());
            return packet;
        }
        return new SamplePacket(request.command(),request.body());
    }

    @Override
    public Packet getHeartBeatPacket(Packet reusable) {
        return getRequestPacket(reusable,new Request.Builder().command(SampleCmd.HEARTBEAT.getValue()).utf8body("{}").build());
    }

    @Override
    public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
        return SamplePacket.build(reusable,source);
    }
}
