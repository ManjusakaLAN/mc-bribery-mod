package com.manjusaka.datapersist.model;

import net.minecraft.nbt.NbtCompound;

public class BriberyTaskInfo {

    public String taskUuid;

    // 申请人
    public String applicantUuid;

    public String applicantName;

    // 贿赂数量
    public int briberyNum;

    public String officialUuid;

    public BriberyTaskInfo(String taskUuid, String applicantUuid, String applicantName, int briberyNum,String officialUuid) {
        this.taskUuid = taskUuid;
        this.applicantUuid = applicantUuid;
        this.applicantName = applicantName;
        this.briberyNum = briberyNum;
        this.officialUuid = officialUuid;
    }

    public BriberyTaskInfo() {
    }

    // 序列化
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("taskUuid", this.taskUuid);
        nbt.putString("applicantUuid", this.applicantUuid);
        nbt.putString("applicantName", this.applicantName);
        nbt.putInt("briberyNum", this.briberyNum);
        nbt.putString("officialUuid", this.officialUuid);
        return nbt;
    }

    // 反序列化
    public static BriberyTaskInfo fromNbt(NbtCompound nbt) {
        BriberyTaskInfo info = new BriberyTaskInfo();
        info.taskUuid = nbt.getString("taskUuid");
        info.applicantUuid = nbt.getString("applicantUuid");
        info.applicantName = nbt.getString("applicantName");
        info.briberyNum = nbt.getInt("briberyNum");
        info.officialUuid = nbt.getString("officialUuid");
        return info;
    }
}
