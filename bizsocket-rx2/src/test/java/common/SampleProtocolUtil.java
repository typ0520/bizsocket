package common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tong on 16/9/12.
 */
public class SampleProtocolUtil {
    public static int getResCode(String s) {
        try {
            JSONObject js = new JSONObject(s);
            if (js.has("code")) {
                int code = js.getInt("code");
                return code;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getMsg(String s) {
        String msg = "";
        try {
            JSONObject obj = new JSONObject(s);
            msg = obj.optString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    //是否是正确的返回
    public static boolean isSuccessResponsePacket(SamplePacket packet) {
        try {
            int code = SampleProtocolUtil.getResCode(packet.getContent());
            if (code == 200) {
                return true;
            }
        } catch (Throwable e) {

        }
        return false;
    }
}
