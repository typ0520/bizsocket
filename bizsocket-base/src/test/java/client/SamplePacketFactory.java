package client;

import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import bizsocket.tcp.Request;
import common.SamplePacket;
import okio.BufferedSource;

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
        return null;
    }

    @Override
    public Packet getRemotePacket(Packet reusable,BufferedSource source) throws IOException {
        return SamplePacket.build(source);
    }
}
