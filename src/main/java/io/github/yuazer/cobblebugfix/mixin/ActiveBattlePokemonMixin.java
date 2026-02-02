package io.github.yuazer.cobblebugfix.mixin;

import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import io.github.yuazer.cobblebugfix.config.CobbleBugFixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ActiveBattlePokemon.class)
public abstract class ActiveBattlePokemonMixin {
    @Redirect(
            method = "getSendOutPosition",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"
            )
    )
    private BlockHitResult cobble$skipExpensiveClip(Level level, ClipContext context) {
        if (CobbleBugFixConfig.shouldSkipSendOutClip()) {
            return BlockHitResult.miss(
                    context.getTo(),
                    Direction.DOWN,
                    BlockPos.containing(context.getTo())
            );
        }
        return level.clip(context);
    }
}
