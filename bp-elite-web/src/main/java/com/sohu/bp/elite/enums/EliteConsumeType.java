package com.sohu.bp.elite.enums;

/**
 * kafka队列消费模式的枚举类，CONSUME_ONCE的groupId相同，CONSUME_ALL的groupId不同
 * @author zhijungou
 * 2017年3月14日
 */
public enum EliteConsumeType implements IEnum {
    CONSUME_ALL(1, "全部消费"),
    CONSUME_ONCE(2, "只消费一次");
    
    private int value;
    private String desc;
    
    private EliteConsumeType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    @Override
    public int getValue() {
        return this.value;
    }
    @Override
    public String getDesc() {
        return this.desc;
    }
}
