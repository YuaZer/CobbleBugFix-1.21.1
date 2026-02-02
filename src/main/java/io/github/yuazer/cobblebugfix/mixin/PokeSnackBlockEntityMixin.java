package io.github.yuazer.cobblebugfix.mixin;

import com.cobblemon.mod.common.block.entity.PokeSnackBlockEntity;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PokeSnackBlockEntity.class)
public class PokeSnackBlockEntityMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("CobbleBugFix");

    @Redirect(
            method = "attemptSpawn(Lnet/minecraft/world/entity/player/Player;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/CompletableFuture;get()Ljava/lang/Object;"
            )
    )
    private Object cobble$getResultSafely(CompletableFuture<?> future) {
        try {
            return future.get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause != null && "Nothing was spawned.".equals(cause.getMessage())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Poke Snack triggered a spawn action but nothing was spawned", cause);
                }
                return null;
            }
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new RuntimeException(cause != null ? cause : ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ex);
        }
    }
}
