package io.github.yuazer.cobblebugfix.mixin;

import io.github.yuazer.cobblebugfix.config.CobbleBugFixConfig;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("CobbleBugFix");

    @Inject(
            method = "canHitEntity(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cobble$skipEntityCollision(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Projectile self = (Projectile) (Object) this;
        if (!(self instanceof FishingHook fishingHook) || fishingHook.level() == null) {
            return;
        }
        String world = fishingHook.level().dimension().location().toString();
        String entityType = entity.getType().toString();
        if (!CobbleBugFixConfig.isConfigLoaded()) {

            return;
        }
        boolean bypass = CobbleBugFixConfig.shouldBypassEntityHooks(fishingHook.level());
        if (bypass) {

            cir.setReturnValue(false);
        } else {

        }
    }
}
