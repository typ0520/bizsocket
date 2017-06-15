package bizsocket.tcp;

/**
 * Created by tong on 17/1/9.
 */
public class DisabledPacketPool implements PacketPool {
    @Override
    public Packet pull() {
        return null;
    }

    @Override
    public void push(Packet packet) {

    }
}
