package com.manjusaka.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.manjusaka.datapersist.PlayerInfoData;
import com.manjusaka.datapersist.model.PlayerInfo;
import com.manjusaka.item.ModItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private static final Logger log = LoggerFactory.getLogger(PlayerEntityMixin.class);

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        cir.setReturnValue(player.getCustomName() == null ? player.getName() : player.getCustomName());
    }

    /**
     *  阻止玩家丢弃工作台通行证
     * @param stack 丢弃的物品
     * @param throwRandomly 是否随机扔出
     * @param retainOwnership 是否保留物品
     * @param cir 回调信息 可取消事件
     */
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if ("tutorial-mod:working_table_passport".equals(Registries.ITEM.getId(stack.getItem()).toString()) && player.isAlive()) {
            cir.cancel();
            player.getInventory().offerOrDrop(stack);
        }
    }

    /**
     * 玩家经验增加
     * @param experience 玩家获取的经验值
     * @param ci ???
     * @param previousLevel 玩家获取的经验值
     */
    @Inject(method = "addExperience", at = @At("TAIL"))
    private void afterAddExperience(int experience, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) int previousLevel) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.experienceLevel % 5 == 0) {
            // 为玩家添加物品
            if (player.getServer() != null){
                PlayerInfoData playerInfoData = PlayerInfoData.get(player.getServer().getOverworld());
                PlayerInfo playerInfo = playerInfoData.playerInfo.get(player.getUuid());
                int lastLevelCardGet = playerInfo.getLastLevelCardGet();
                if(player.experienceLevel > lastLevelCardGet){
                    player.giveItemStack(new ItemStack(ModItems.WORKING_TABLE_PASSPORT));
                    playerInfo.setLastLevelCardGet(lastLevelCardGet + 5);
                    playerInfoData.updatePlayerRole(player.getUuid(), playerInfo);
                    player.getServer().getOverworld().getPersistentStateManager().save();
                    log.info("为玩家{},id:{}, 发送1张合成许可证" , player.getName().getString(), player.getUuid());
                }
            }
        }
    }
}