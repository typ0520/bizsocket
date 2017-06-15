package bizsocket.android;

import android.os.Handler;
import bizsocket.core.*;
import bizsocket.logger.LoggerFactory;
import bizsocket.tcp.Packet;
import bizsocket.tcp.Request;

/**
 * Created by tong on 17/1/9.
 */
public abstract class AndroidBizSocket extends AbstractBizSocket {
    private final Handler handler = new Handler();

    static {
        LoggerFactory.setDefaultLoggerType(AndroidLogger.class);
    }

    public AndroidBizSocket() {
    }

    public AndroidBizSocket(Configuration configuration) {
        super(configuration);
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        if (configuration != null) {
            AndroidLogger.LOG_ENABLE = configuration.isLogEnable();
        }
        super.setConfiguration(configuration);
    }

    @Override
    public RequestQueue createRequestQueue(AbstractBizSocket bizSocket) {
        return new AndroidRequestQueue(bizSocket);
    }

    @Override
    protected RequestContext obtainRequestContext(Request request, Packet requestPacket, ResponseHandler responseHandler) {
        return new AndroidRequestContext(request,requestPacket,responseHandler,handler);
    }
}
