package us.timinc.mc.cobblemon.counter

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.scheduling.ScheduledTask
import com.cobblemon.mod.common.api.scheduling.ServerTaskTracker
import com.cobblemon.mod.common.api.storage.player.PlayerInstancedDataStoreType
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import us.timinc.mc.cobblemon.counter.config.ConfigBuilder
import us.timinc.mc.cobblemon.counter.config.CounterConfig
import us.timinc.mc.cobblemon.counter.event.CounterEvents
import us.timinc.mc.cobblemon.counter.item.CounterItems
import us.timinc.mc.cobblemon.counter.storage.PlayerInstancedDataStores

object CounterMod : ModInitializer {
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    const val MOD_ID = "cobbled_counter"

    val saveTasks = mutableMapOf<PlayerInstancedDataStoreType, ScheduledTask>()

    private var logger: Logger = LogManager.getLogger(MOD_ID)
    var config: CounterConfig = ConfigBuilder.load(CounterConfig::class.java, MOD_ID)

    override fun onInitialize() {
        CounterEvents.register()
        CounterItems.register()
        saveTasks[PlayerInstancedDataStores.COUNTER] = ScheduledTask.Builder()
            .execute { Cobblemon.playerDataManager.saveAllOfOneType(PlayerInstancedDataStores.COUNTER) }
            .delay(30f)
            .interval(120f)
            .infiniteIterations()
            .tracker(ServerTaskTracker)
            .build()
    }

    fun debug(msg: String) {
        if (!config.debug) return
        logger.info(msg)
    }

    fun modResource(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}