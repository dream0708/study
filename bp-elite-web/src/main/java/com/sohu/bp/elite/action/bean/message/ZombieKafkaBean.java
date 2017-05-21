package com.sohu.bp.elite.action.bean.message;

/**
 * Created by nicholastang on 2017/3/30.
 */
public class ZombieKafkaBean {
    private String msgType = "normal";
    private Long generatorId;
    private Integer generatorType;
    private Long materialId;
    private Integer materialType;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Long getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(Long generatorId) {
        this.generatorId = generatorId;
    }

    public Integer getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(Integer generatorType) {
        this.generatorType = generatorType;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Integer getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Integer materialType) {
        this.materialType = materialType;
    }
}
