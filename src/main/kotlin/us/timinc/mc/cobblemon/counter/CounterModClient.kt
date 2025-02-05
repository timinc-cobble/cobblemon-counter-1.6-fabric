package us.timinc.mc.cobblemon.counter

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.platform.events.ClientPlayerEvent
import com.cobblemon.mod.common.platform.events.PlatformEvents
import net.fabricmc.api.ClientModInitializer
import us.timinc.mc.cobblemon.counter.api.ClientCounterManager
import us.timinc.mc.cobblemon.counter.storage.PlayerInstancedDataStores

object CounterModClient : ClientModInitializer {
    var clientCounterData: ClientCounterManager = ClientCounterManager(mutableMapOf())

    fun onLogin(event: ClientPlayerEvent.Login) {
        clientCounterData = ClientCounterManager(mutableMapOf())
    }

    override fun onInitializeClient() {
        PlayerInstancedDataStores.COUNTER
        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe(Priority.LOWEST, ::onLogin)
    }
}