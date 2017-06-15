package bizsocket.android;

import android.util.Log;
import bizsocket.logger.Logger;

/**
 * Created by tong on 17/1/9.
 */
public class AndroidLogger extends Logger {
    public static boolean LOG_ENABLE = true;

    public AndroidLogger(String tag) {
        super(tag);
    }

    @Override
    public boolean isEnable() {
        return LOG_ENABLE;
    }

    @Override
    public void debug(String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        Log.d(tag,msg);
    }

    @Override
    public void info(String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        Log.i(tag,msg);
    }

    @Override
    public void warn(String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        Log.w(tag,msg);
    }

    @Override
    public void error(String msg) {
        if (!LOG_ENABLE) {
            return;
        }
        Log.e(tag,msg);
    }
}
