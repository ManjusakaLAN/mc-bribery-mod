package com.manjusaka.datapersist.model;

import net.minecraft.nbt.NbtCompound;


public class PlayerInfo {
    // 玩家名称
    public String name;
    // 玩家UUID
    public String uuid;
    // 玩家等级
    public int level;
    // 上一次发送 制造台合成卡的等级
    public int lastLevelCardGet;
    // 离线时 得到的许可证实例
    public int offlinePermits;

    public String role;
    public PlayerInfo() {
    }

    public PlayerInfo(String name, String uuid, int level, int lastLevelCardGet, String role) {
        this.name = name;
        this.uuid = uuid;
        this.level = level;
        this.lastLevelCardGet = lastLevelCardGet;
        this.role = role;
        this.offlinePermits = 0;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getOfflinePermits() {
        return offlinePermits;
    }

    public void setOfflinePermits(int offlinePermits) {
        this.offlinePermits = offlinePermits;
    }

    public int getLastLevelCardGet() {
        return lastLevelCardGet;
    }

    public void setLastLevelCardGet(int lastLevelCardGet) {
        this.lastLevelCardGet = lastLevelCardGet;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", level=" + level +
                ", lastLevelCardGet=" + lastLevelCardGet +
                ", offlinePermits=" + offlinePermits +
                ", role='" + role + '\'' +
                '}';
    }

    // 序列化
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", this.name);
        nbt.putString("uuid", this.uuid);
        nbt.putInt("level", this.level);
        nbt.putInt("lastLevelCardGet", this.lastLevelCardGet);
        nbt.putString("role", this.role);
        nbt.putInt("offlinePermits", this.offlinePermits);
        return nbt;
    }

    // 反序列化
    public static PlayerInfo fromNbt(NbtCompound nbt) {
        PlayerInfo info = new PlayerInfo();
        info.name = nbt.getString("name");
        info.uuid = nbt.getString("uuid");
        info.level = nbt.getInt("level");
        info.lastLevelCardGet = nbt.getInt("lastLevelCardGet");
        info.role = nbt.getString("role");
        info.offlinePermits = nbt.getInt("offlinePermits");
        return info;
    }
}
