package com.manjusaka.datapersist;

import com.manjusaka.datapersist.model.BriberyTaskInfo;
import com.manjusaka.datapersist.model.BriberyTaskResultInfo;
import com.manjusaka.datapersist.model.PlayerInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BriberyTaskRecorder extends PersistentState {

    public final Map<UUID, List<BriberyTaskResultInfo>> officialTaskRecorderMap = new ConcurrentHashMap<>();

    public static String store_key = "bribery_task_result_info";

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");


    public static final Lock lock1 = new ReentrantLock();

    private static final Logger log = LoggerFactory.getLogger(BriberyTaskRecorder.class);
    /**
     * 添加任务结果
     * @param officialId 处理人uuid
     * @param taskResult 任务结果
     */
    public void recordTaskResult(UUID officialId, BriberyTaskResultInfo taskResult) {

        try {
            lock1.lock();
            List<BriberyTaskResultInfo> briberyTaskResultInfos = officialTaskRecorderMap.get(officialId);
            if (briberyTaskResultInfos == null){
                briberyTaskResultInfos = new CopyOnWriteArrayList<>();
            }
            briberyTaskResultInfos.add(taskResult);
            officialTaskRecorderMap.put(officialId, briberyTaskResultInfos);
        } finally {
            lock1.unlock();
        }
        markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound taskResultNbt = new NbtCompound();
        officialTaskRecorderMap.forEach((uuid, taskResults) -> {
            NbtList taskNbtList = new NbtList();
            taskResults.forEach(task -> taskNbtList.add(task.toNbt()));
            taskResultNbt.put(uuid.toString(),taskNbtList);
        });
        nbt.put("task_result_record", taskResultNbt);
        return nbt;
    }

    public static BriberyTaskRecorder fromNbt(NbtCompound nbt) {

        BriberyTaskRecorder state = new BriberyTaskRecorder();
        NbtCompound rolesNbt = nbt.getCompound("task_result_record");
        rolesNbt.getKeys().forEach(key ->{
            List<BriberyTaskResultInfo> taskResults = new CopyOnWriteArrayList<>();
            NbtList taskNbt = rolesNbt.getList(key, NbtElement.COMPOUND_TYPE);
            taskNbt.forEach(taskNbtElement -> taskResults.add(BriberyTaskResultInfo.fromNbt((NbtCompound) taskNbtElement)));
            state.officialTaskRecorderMap.put(UUID.fromString(key), taskResults);
        });
        return state;
    }

    public static BriberyTaskRecorder get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
                BriberyTaskRecorder::fromNbt,
                BriberyTaskRecorder::new,
                store_key + dateFormat.format(new Date())
        );
    }
}
