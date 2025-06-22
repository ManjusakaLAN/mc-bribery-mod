package com.manjusaka.mixin;

import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin extends ScreenHandler {

    protected CraftingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }




    @Inject(method = "onContentChanged", at = @At("HEAD"))
    private void onCraftingMatrixChanged(CallbackInfo ci) {
        CraftingScreenHandler handler = (CraftingScreenHandler) (Object) this;


        // 遍历所有槽位，查看哪些发生了变化
//        for (int i = 0; i < handler.slots.size(); i++) {
//            System.out.println("槽位 " + i + " 内容: " + handler.getSlot(i).getStack().getItem().getName().getString());
//            Slot slot = handler.getSlot(i);
//            ItemStack stack = slot.getStack();
//
//            if (slot.hasStack() || !stack.isEmpty()) {
//                System.out.println("槽位 " + i + " 内容发生变化: " + stack.getItem().getName().getString());
//            }
//        }

//            System.out.println("槽位 " + 0 + " 内容: " + handler.getSlot(0).getStack().getItem().getName().getString());



//        System.out.println("onContentChanged() 被调用");
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"))
    private void onCraftingMatrixChanged2(CallbackInfo ci) {
        CraftingScreenHandler handler = (CraftingScreenHandler) (Object) this;
//        System.out.println("槽位 " + 0 + " 内容: " + handler.getSlot(0).getStack().getItem().getName().getString());


        // 监听用户点击
        if (handler.getSlot(0).hasStack()){
//            System.out.println("用户点击了槽位6666 " + 0 + " 内容: " + handler.getSlot(0).getStack().getItem().getName().getString());
        }


    }
}