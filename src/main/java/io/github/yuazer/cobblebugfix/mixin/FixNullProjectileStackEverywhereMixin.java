package io.github.yuazer.cobblebugfix.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 SoulsWeaponry 的 death_spiral 等投射物把 AbstractArrow 内部 ItemStack 置空导致：
 * 1) 保存NBT时 NPE
 * 2) EternalStarlight tick 时调用 AbstractArrow.getXXX() 返回 null NPE
 *
 * 策略：任何时候发现为 null，都兜底替换成“木棒”(Items.STICK)
 */
@Mixin(AbstractArrow.class)
public abstract class FixNullProjectileStackEverywhereMixin {

    /**
     * ⚠️ 这里字段名需要按你 Mojmap 的 AbstractArrow 实际字段名替换。
     * 常见候选（不同版本可能不同）：
     * - pickupItemStack
     * - pickupItem
     * - pickup
     *
     * 你在 IDE 里打开 AbstractArrow，搜 "ItemStack" 基本就能找到。
     */
    @Shadow private ItemStack pickupItemStack;

    /** 统一生成木棒 ItemStack（避免重复 new） */
    private static ItemStack zax$stick() {
        return new ItemStack(Items.STICK);
    }

    /**
     * 兜底点1：每 tick 开头先把 null 修掉，防止任何模组 tick 回调时读到 null
     *
     * Mojmap 常见方法名：tick()
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void zax$fixNullStackBeforeTick(CallbackInfo ci) {
        if (this.pickupItemStack == null) {
            this.pickupItemStack = zax$stick();
        }
    }

    /**
     * 兜底点2：保存NBT前修一次（你之前的修复点）
     *
     * Mojmap 常见方法名（二选一，看你版本）：
     * - addAdditionalSaveData(CompoundTag)
     * - saveAdditional(CompoundTag)
     */
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void zax$fixNullStackBeforeSave(CompoundTag tag, CallbackInfo ci) {
        if (this.pickupItemStack == null) {
            this.pickupItemStack = zax$stick();
        }
    }

    /**
     * 兜底点3：修复你这次报错里提到的 getter：AbstractArrow.method_54759()
     * 让它永远不返回 null（返回木棒）
     *
     * Mojmap 下它很可能叫：
     * - getPickupItemStack()
     * - getPickupItem()
     *
     * 你可以先用 @Inject(method="getPickupItemStack", at=@At("RETURN"), cancellable=true)
     * 如果编译不通过，再改成实际方法名。
     */
    @Inject(method = "getPickupItem", at = @At("RETURN"), cancellable = true)
    private void zax$fixNullReturnOfPickupGetter(CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() == null) {
            // 同时把字段也修回去，避免下次又返回 null
            this.pickupItemStack = zax$stick();
            cir.setReturnValue(this.pickupItemStack);
        }
    }
}