package bizsocket.android;

import android.os.Handler;
import android.os.Looper;
import bizsocket.core.AbstractBizSocket;
import bizsocket.core.RequestQueue;
import bizsocket.tcp.Packet;

/**
 * Created by tong on 17/1/9.
 */
public class AndroidRequestQueue extends RequestQueue {
    private final Handler handler = new Handler();

    public AndroidRequestQueue(AbstractBizSocket bizSocket) {
        super(bizSocket);
    }

    @Override
    public void dispatchPacket(final Packet responsePacket) {
        //dispatch on main thread
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            AndroidRequestQueue.super.dispatchPacket(responsePacket);
        }
        else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AndroidRequestQueue.super.dispatchPacket(responsePacket);
                }
            });
        }
    }
}
