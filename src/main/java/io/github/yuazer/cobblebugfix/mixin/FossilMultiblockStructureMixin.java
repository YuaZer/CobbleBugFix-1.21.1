package io.github.yuazer.cobblebugfix.mixin;

import com.cobblemon.mod.common.block.multiblock.FossilMultiblockStructure;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.nbt.CompoundTag;

@Mixin(FossilMultiblockStructure.class)
public abstract class FossilMultiblockStructureMixin {

    /**
     * 用 ThreadLocal 避免并发/嵌套调用导致的 registryLookup 被覆盖或串线
     */
    @Unique
    private static final ThreadLocal<HolderLookup.Provider> cobble$registryLookup = new ThreadLocal<>();

    @Inject(
            method = "writeToNbt(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("HEAD")
    )
    private void cobble$storeRegistryLookup(HolderLookup.Provider registryLookup, CallbackInfoReturnable<CompoundTag> cir) {
        cobble$registryLookup.set(registryLookup);
    }

    @Redirect(
            method = "writeToNbt(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
                    remap = false
            )
    )
    @SuppressWarnings({"rawtypes", "unchecked"})
    private DataResult<?> cobble$encodeWithRegistry(Codec codec, DynamicOps ops, Object input) {
        HolderLookup.Provider provider = cobble$registryLookup.get();
        if (provider != null) {
            DynamicOps registryOps = RegistryOps.create(ops, provider);
            return codec.encodeStart(registryOps, input);
        }
        return codec.encodeStart(ops, input);
    }


    @Inject(
            method = "writeToNbt(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("RETURN")
    )
    private void cobble$clearRegistryLookup(HolderLookup.Provider registryLookup, CallbackInfoReturnable<CompoundTag> cir) {
        cobble$registryLookup.remove();
    }
}
