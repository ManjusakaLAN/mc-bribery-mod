package com.manjusaka.mixin;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class SlotMixin {

    @Shadow
    @Final
    private PlayerEntity player; // 假设CraftingResultSlot中包含玩家对象

    // 获取玩家等级的方法（示例）
    @Unique
    private int getPlayerLevel(PlayerEntity player) {
        // 这里是示例实现，具体逻辑根据你的游戏机制决定
        return player.experienceLevel;
    }

    @Inject(method = "onTakeItem" , at = @At("HEAD"), cancellable = true)
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {

        //判断当前玩家是否打开的是合成台页面
        if (this.player.currentScreenHandler instanceof CraftingScreenHandler) {
            boolean compositeFlag = false;
            // 获取当前玩家所有背包的物品
            for (Slot slot : player.currentScreenHandler.slots) {
                if("tutorial-mod:working_table_passport".equals(Registries.ITEM.getId(slot.getStack().getItem()).toString())){
                    // 物品数量减1
                    slot.getStack().decrement(1);
                    // 跳出循环
                    // 可以合成,在聊天框打印
                    player.sendMessage(Text.literal("合成成功"));
                    compositeFlag = true;
                    break;
                }
            }

            if(!compositeFlag){
                // 取消合成
                player.sendMessage(Text.literal("合成许可证物品数量不足"));
                stack.setCount(0);
                ci.cancel();
            }
        }
    }
}