package com.manjusaka.datapersist;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;



public class WorldProperties extends PersistentState {

    // briberySuccessfulRate 贿赂成功系数: 0-100
    public  int briberySuccessfulRate;

    // stage 阶段: stage1-stage3 实验阶段
    public  int stage;
    // 是否完成了一整伦的角色分配
    public int assignThreshold;
    // 分配官员的数量
    public int assignNum;

    public int woodCmdPermit;

    public int getBriberySuccessfulRate() {
        return briberySuccessfulRate;
    }

    public WorldProperties() {
        this.briberySuccessfulRate = 90;
        this.stage = 1;
        this.assignThreshold = 1;
        this.assignNum = 10;
        this.woodCmdPermit = 0;
        markDirty();
    }

    public void setBriberySuccessfulRate(int briberySuccessfulRate) {
        this.briberySuccessfulRate = briberySuccessfulRate;
        markDirty();
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
        markDirty();
    }

    public int getAssignThreshold() {
        return assignThreshold;
    }

    public void setAssignThreshold(int assignThreshold) {
        this.assignThreshold = assignThreshold;
        markDirty();
    }

    public int getAssignNum() {
        return assignNum;
    }

    public void setAssignNum(int assignNum) {
        this.assignNum = assignNum;
        markDirty();
    }

    public int getWoodCmdPermit() {
        return woodCmdPermit;
    }

    public void setWoodCmdPermit(int woodCmdPermit) {
        this.woodCmdPermit = woodCmdPermit;
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound worldNbt = new NbtCompound();
        worldNbt.putInt("briberySuccessfulRate", briberySuccessfulRate);
        worldNbt.putInt("stage", stage);
        worldNbt.putInt("assignThreshold", assignThreshold);
        worldNbt.putInt("assignNum", assignNum);
        worldNbt.putInt("woodCmdPermit", woodCmdPermit);
        nbt.put("word_properties", worldNbt);

        return nbt;
    }

    public static WorldProperties fromNbt(NbtCompound nbt) {
        WorldProperties worldProperties = new WorldProperties();
        NbtCompound wordProperties = nbt.getCompound("word_properties");

        wordProperties.getKeys().forEach(key ->{
                    if(key.equals("briberySuccessfulRate")){
                        worldProperties.briberySuccessfulRate = wordProperties.getInt(key);
                    }
                    if (key.equals("stage")){
                        worldProperties.stage = wordProperties.getInt(key);
                    }
                    if (key.equals("assignThreshold")){
                        worldProperties.assignThreshold = wordProperties.getInt(key);
                    }
                    if (key.equals("assignNum")){
                        worldProperties.assignNum = wordProperties.getInt(key);
                    }
                    if (key.equals("woodCmdPermit")){
                        worldProperties.woodCmdPermit = wordProperties.getInt(key);
                    }
                }
                );
        return worldProperties;
    }

    public static WorldProperties get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                WorldProperties::fromNbt,
                WorldProperties::new,
                "word_properties"
        );
    }
}
