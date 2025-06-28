package com.manjusaka.datapersist;

import com.manjusaka.datapersist.model.PlayerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerInfoData extends PersistentState {

    private static final Logger log = LoggerFactory.getLogger(PlayerInfoData.class);

    public final Map<UUID, PlayerInfo> playerInfo = new ConcurrentHashMap<>(); // 存储玩家身份

    public void setPlayerRole(UUID playerId, PlayerInfo role) {
        playerInfo.put(playerId, role);
        markDirty(); // 标记需要保存
    }

    public void updatePlayerRole(UUID playerId, PlayerInfo role) {
        playerInfo.put(playerId, role);
        markDirty(); // 标记需要保存
    }

    public PlayerInfo getPlayerRole(UUID playerId) {
        return playerInfo.get(playerId);
    }




    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound rolesNbt = new NbtCompound();
        playerInfo.forEach((id, role) -> rolesNbt.put(id.toString(), role.toNbt()));
        nbt.put("player_info_map", rolesNbt);
        return nbt;
    }

    public static PlayerInfoData fromNbt(NbtCompound nbt) {
        log.info("Loading server data...");
        PlayerInfoData state = new PlayerInfoData();
        NbtCompound rolesNbt = nbt.getCompound("player_info_map");
        rolesNbt.getKeys().forEach(key ->
                state.playerInfo.put(UUID.fromString(key), PlayerInfo.fromNbt(rolesNbt.getCompound(key))));
        return state;
    }

    public static PlayerInfoData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                PlayerInfoData::fromNbt,
                PlayerInfoData::new,
                "play_info_data"
        );
    }

}