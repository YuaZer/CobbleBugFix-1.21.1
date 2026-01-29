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

/**
 * 修复 SoulsWeaponry某些持久投射物在保存实体(NBT)时因为内部ItemStack为 null导致 NPE：
 * 在原版写入NBT前兜底，把 null 修正为 ItemStack.EMPTY。
 */
@Mixin(AbstractArrow.class)
public abstract class FixNullProjectileStackOnSaveMixin {

    @Shadow private ItemStack pickupItemStack;

    /**
     * Mojmap 下实体保存常见方法名：addAdditionalSaveData
     * 如果你版本里是 saveAdditional / addAdditionalSaveData 之一，按实际方法名改。
     */
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void zax$fixNullStackBeforeSave(CompoundTag tag, CallbackInfo ci) {
        if (this.pickupItemStack == null) {
            this.pickupItemStack = new ItemStack(Items.STICK);
        }
    }
}