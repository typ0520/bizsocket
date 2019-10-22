package bizsocket.sample.j2se.common;

/**
 * 命令枚举
 */
public enum SampleCmd {
    HEARTBEAT(100,"心跳"),
    CREATE_ORDER(300, "创建订单"),
    QUERY_ORDER_LIST(400, "查询订单列表"),
    QUERY_ORDER_TYPE(500, "查询当前持仓单类型"),

    NOTIFY_DISCONNECT(3000,"服务器将要断连的通知"),
    NOTIFY_PRICE(2000, "报价推送")
    ;

    private int value;
    private String desc;

    SampleCmd(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }

    public static SampleCmd fromValue(int value) {
        for (SampleCmd SampleCmd : values()) {
            if (SampleCmd.getValue() == value) {
                return SampleCmd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "SampleCmd{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}


