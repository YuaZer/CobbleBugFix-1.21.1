package io.github.yuazer.cobblebugfix.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackSaveMixin {

    /**
     * Fix: allow empty ItemStack to be saved without throwing IllegalStateException
     * Mojang mappings: ItemStack#save(HolderLookup.Provider)
     */
    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cobble$allowEmptySave(HolderLookup.Provider provider, CallbackInfoReturnable<Tag> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.isEmpty()) {
            // Vanilla's saveOptional does exactly this. We do it here to avoid exception from save().
            cir.setReturnValue(new CompoundTag());
        }
    }

    /**
     * Fix: allow empty ItemStack to be saved without throwing IllegalStateException
     * Mojang mappings: ItemStack#save(HolderLookup.Provider, Tag)
     */
    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cobble$allowEmptySaveInto(HolderLookup.Provider provider, Tag tag, CallbackInfoReturnable<Tag> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.isEmpty()) {
            // If caller supplied a CompoundTag, reuse it; otherwise return a fresh CompoundTag.
            cir.setReturnValue(tag instanceof CompoundTag ? tag : new CompoundTag());
        }
    }
}
