package bizsocket.sample.j2se.common;

import bizsocket.base.Body;
import bizsocket.base.Request;
import io.reactivex.Observable;
import okio.ByteString;
import org.json.JSONObject;

/**
 * Created by tong on 17/6/15.
 */
public interface SampleService {
    @Request(cmd = 300,desc = "创建订单")
    Observable<JSONObject> createOrder(@Body String params);

    @Request(cmd = 300,desc = "创建订单")
    Observable<JSONObject> createOrder2(@Body JSONObject params);

    @Request(cmd = 300,desc = "创建订单")
    Observable<JSONObject> createOrder3(@Body ByteString params);

    @Request(cmd = 400,desc = "查询订单列表")
    Observable<JSONObject> queryOrderList(@Body JSONObject params);
}
