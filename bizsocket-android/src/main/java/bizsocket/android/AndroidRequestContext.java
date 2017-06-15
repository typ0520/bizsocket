package bizsocket.android;

import android.os.Handler;
import bizsocket.core.RequestContext;
import bizsocket.core.ResponseHandler;
import bizsocket.tcp.Packet;
import bizsocket.tcp.Request;

/**
 * Created by tong on 17/1/9.
 */
public class AndroidRequestContext extends RequestContext {
    private final Handler handler;

    private final Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            callRequestTimeout();
        }
    };

    public AndroidRequestContext(Request request, Packet requestPacket, ResponseHandler responseHandler, Handler handler) {
        super(request, requestPacket, responseHandler);
        this.handler = handler;
    }

    @Override
    public void startTimeoutTimer() {
        handler.postDelayed(timeoutRunnable,readTimeout * 1000);
    }

    @Override
    public void onRemoveFromQueue() {
        logger.debug("remove from queue: " + toString());
        handler.removeCallbacks(timeoutRunnable);
    }
}
