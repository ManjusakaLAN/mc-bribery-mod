package com.manjusaka.datapersist;

import com.manjusaka.datapersist.model.PlayerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BriberyTaskRecorder extends PersistentState {


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound rolesNbt = new NbtCompound();
//        playerInfo.forEach((id, role) -> rolesNbt.put(id.toString(), role.toNbt()));
//        nbt.put("player_info_map", rolesNbt);
        return nbt;
    }

    public static PlayerInfoData fromNbt(NbtCompound nbt) {

        PlayerInfoData state = new PlayerInfoData();
        NbtCompound rolesNbt = nbt.getCompound("player_info_map");
        rolesNbt.getKeys().forEach(key ->
                state.playerInfo.put(UUID.fromString(key), PlayerInfo.fromNbt(rolesNbt.getCompound(key))));
        return state;
    }

    public static PlayerInfoData get(ServerWorld world) {
        String persistentStateId = "bribery_task_record" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return world.getPersistentStateManager().getOrCreate(
                PlayerInfoData::fromNbt,
                PlayerInfoData::new,
                "play_info_data"
        );
    }
}
