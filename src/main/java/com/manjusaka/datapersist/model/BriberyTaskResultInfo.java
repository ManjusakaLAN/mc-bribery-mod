package com.manjusaka.datapersist.model;

import net.minecraft.nbt.NbtCompound;

public class BriberyTaskResultInfo {

    public String taskUuid;

    // 申请人
    public String applicantUuid;

    public String applicantName;

    // 贿赂数量
    public int briberyNum;

    // 官员id
    public String officialUuid;

    // 官员名称
    public String officialName;

    // 档次任务是否被接受
    public Boolean isAccepted;

    // 成功概率
    public int successRatio;

    // 贿赂成功后 最终是否获得了奖励
    public Boolean isSuccess;

    // 任务处理时间
    public String handlerDate;


    public BriberyTaskResultInfo() {
    }

    // 序列化
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("taskUuid", this.taskUuid);
        nbt.putString("applicantUuid", this.applicantUuid);
        nbt.putString("applicantName", this.applicantName);
        nbt.putInt("briberyNum", this.briberyNum);
        nbt.putString("officialUuid", this.officialUuid);
        nbt.putString("officialName", this.officialName);
        nbt.putBoolean("isAccepted", this.isAccepted);
        nbt.putInt("successRatio", this.successRatio);
        nbt.putBoolean("isSuccess", this.isSuccess);
        nbt.putString("handlerDate", this.handlerDate);
        return nbt;
    }

    public static BriberyTaskResultInfo fromNbt(NbtCompound nbt) {
        BriberyTaskResultInfo info = new BriberyTaskResultInfo();
        info.taskUuid = nbt.getString("taskUuid");
        info.applicantUuid = nbt.getString("applicantUuid");
        info.applicantName = nbt.getString("applicantName");
        info.briberyNum = nbt.getInt("briberyNum");
        info.officialUuid = nbt.getString("officialUuid");
        info.officialName = nbt.getString("officialName");
        info.isAccepted = nbt.getBoolean("isAccepted");
        info.successRatio = nbt.getInt("successRatio");
        info.isSuccess = nbt.getBoolean("isSuccess");
        info.handlerDate = nbt.getString("handlerDate");
        return info;
    }
}
