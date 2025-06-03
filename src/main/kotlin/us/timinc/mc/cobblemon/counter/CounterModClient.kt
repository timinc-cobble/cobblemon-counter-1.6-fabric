package us.timinc.mc.cobblemon.counter

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.client.tooltips.TooltipManager
import com.cobblemon.mod.common.platform.events.ClientPlayerEvent
import com.cobblemon.mod.common.platform.events.PlatformEvents
import net.fabricmc.api.ClientModInitializer
import us.timinc.mc.cobblemon.counter.api.ClientCounterManager
import us.timinc.mc.cobblemon.counter.config.ClientCounterConfig
import us.timinc.mc.cobblemon.counter.config.ConfigBuilder
import us.timinc.mc.cobblemon.counter.item.CounterItems
import us.timinc.mc.cobblemon.counter.item.CounterTooltipGenerator
import us.timinc.mc.cobblemon.counter.storage.PlayerInstancedDataStores

object CounterModClient : ClientModInitializer {
    const val MOD_ID = "cobbled_counter_client"
    var clientCounterData: ClientCounterManager = ClientCounterManager(mutableMapOf(), emptySet())
    var config: ClientCounterConfig = ConfigBuilder.load(ClientCounterConfig::class.java, MOD_ID)

    fun onLogin(event: ClientPlayerEvent.Login) {
        clientCounterData = ClientCounterManager(mutableMapOf(), emptySet())
    }

    override fun onInitializeClient() {
        PlayerInstancedDataStores.COUNTER
        CounterItems.register()
        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe(Priority.LOWEST, ::onLogin)
        TooltipManager.registerTooltipGenerator(CounterTooltipGenerator)
    }
}