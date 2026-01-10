package io.github.yuazer.cobblebugfix

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

class Cobblebugfix : ModInitializer {
    override fun onInitialize() {
        val logger = LoggerFactory.getLogger("CobbleBugFix")
        logger.info("CobbleBugFix initialized")
    }
}
