package common;

/**
 * 命令枚举
 */
public enum SampleCmd {
    UNKNOWN(-1, ""),
    INIT(8888, "初始化命令"),
    NOTIFY_PRICE(1, "报价"),
    CREATE_ORDER(2, "创建订单"),
    QUERY_ORDER_LIST(10006, "查询订单列表"),
    QUERY_ORDER_TYPE(51009, "查询当前持仓单类型"),
    HEARTBEAT(9999, "心跳");

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
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return "SampleCmd{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}


