package com.manjusaka.datapersist;

import com.manjusaka.datapersist.model.BriberyTaskInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BriberyTaskInfoData extends PersistentState {
    private static final Logger log = LoggerFactory.getLogger(BriberyTaskInfoData.class);

    public final Map<UUID, List<BriberyTaskInfo>> officialTask = new ConcurrentHashMap<>();
    /**
     * 提交任务
     * @param officialId 官员id
     * @param task 任务
     */
    public void submitTask(UUID officialId, BriberyTaskInfo task) {
        officialTask.computeIfAbsent(officialId, k -> new CopyOnWriteArrayList<>()).add(task);
        markDirty();
    }

    /**
     * 处理完任务后移除
     * @param playerId 官员id
     * @param task 任务
     */
    public void removeTask(UUID playerId, BriberyTaskInfo task) {
        officialTask.computeIfPresent(playerId, (uuid, list) -> {
            list.remove(task);
            return list.isEmpty() ? Collections.emptyList() : list;
        });
        markDirty();
    }

    /**
     * 获取任务
     * @param playerId 玩家ID
     * @return 任务列表
     */
    public List<BriberyTaskInfo> getTask(UUID playerId) {
        return officialTask.get(playerId);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound rolesNbt = new NbtCompound();
        officialTask.forEach((uuid, tasks) ->{
            NbtList taskNbtList = new NbtList();
            tasks.forEach(task -> taskNbtList.add(task.toNbt()));
            rolesNbt.put(uuid.toString(), taskNbtList);
        } );
        nbt.put("official_task_info_map", rolesNbt);
        return nbt;
    }

    public static BriberyTaskInfoData fromNbt(NbtCompound nbt) {
        BriberyTaskInfoData state = new BriberyTaskInfoData();
        NbtCompound rolesNbt = nbt.getCompound("official_task_info_map");
        rolesNbt.getKeys().forEach(key -> {
            List<BriberyTaskInfo> taskNbtList = new ArrayList<>();
            NbtList taskNbt = rolesNbt.getList(key, NbtElement.COMPOUND_TYPE);
            taskNbt.forEach(taskNbtElement -> taskNbtList.add(BriberyTaskInfo.fromNbt((NbtCompound) taskNbtElement)));
             state. officialTask.put( UUID.fromString(key), taskNbtList);
        });

        return state;
    }

    public static BriberyTaskInfoData get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
               BriberyTaskInfoData::fromNbt,
                BriberyTaskInfoData::new,
                "official_task_info_map"
        );
    }
}
