package common;

import bizsocket.core.PacketValidator;
import bizsocket.tcp.Packet;

/**
 * Created by tong on 16/10/6.
 */
public class SampleBizPacketValidator implements PacketValidator {
    @Override
    public boolean verify(Packet packet) {
        return SampleProtocolUtil.isSuccessResponsePacket((SamplePacket) packet);
    }
}
