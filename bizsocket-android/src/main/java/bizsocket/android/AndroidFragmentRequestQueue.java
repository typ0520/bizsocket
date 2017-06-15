package bizsocket.android;

import android.os.Handler;
import android.os.Looper;
import bizsocket.core.AbstractBizSocket;
import bizsocket.core.AbstractFragmentRequestQueue;
import bizsocket.tcp.Packet;

/**
 * Created by tong on 17/1/9.
 */
public abstract class AndroidFragmentRequestQueue<T extends Packet> extends AbstractFragmentRequestQueue<T> {
    private final Handler handler = new Handler();

    public AndroidFragmentRequestQueue(AbstractBizSocket bizSocket) {
        super(bizSocket);
    }

    @Override
    public void dispatchPacket(final Packet responsePacket) {
        //dispatch on main thread
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            AndroidFragmentRequestQueue.super.dispatchPacket(responsePacket);
        }
        else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AndroidFragmentRequestQueue.super.dispatchPacket(responsePacket);
                }
            });
        }
    }
}
