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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BriberyTaskInfoData extends PersistentState {
    private static final Logger log = LoggerFactory.getLogger(BriberyTaskInfoData.class);

    public final Map<UUID, List<BriberyTaskInfo>> officialTask = new ConcurrentHashMap<>();

    public static final Lock lock1 = new ReentrantLock();
    /**
     * 提交任务
     * @param officialId 官员id
     * @param task 任务
     */
    public void submitTask(UUID officialId, BriberyTaskInfo task) {
        try {
            lock1.lock();
            log.info("t{}", task);
            List<BriberyTaskInfo> briberyTaskInfos = officialTask.get(officialId);
            log.info("l{}", briberyTaskInfos);
            if(briberyTaskInfos == null){
                briberyTaskInfos = new CopyOnWriteArrayList<>();
            }
            briberyTaskInfos.add(task);

            officialTask.put(officialId, briberyTaskInfos);

        } finally {
            lock1.unlock();
        }
        log.info("{}", officialTask);
        markDirty();
    }

    /**
     * 处理完任务后移除
     * @param officialId 官员id
     * @param task 任务
     */
    public void removeTask(UUID officialId, BriberyTaskInfo task) {
        try {
            lock1.lock();
            List<BriberyTaskInfo> briberyTaskInfos = officialTask.get(officialId);
            if (briberyTaskInfos == null){
                return;
            }
            for (BriberyTaskInfo briberyTaskInfo : briberyTaskInfos) {
                if (briberyTaskInfo.taskUuid.equals(task.taskUuid)){
                   task = briberyTaskInfo;
                    break;
                }
            }
            briberyTaskInfos.remove(task);
            officialTask.put(officialId, briberyTaskInfos);
        } finally {
            lock1.unlock();
        }
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
