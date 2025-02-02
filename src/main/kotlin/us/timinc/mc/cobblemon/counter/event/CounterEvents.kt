package us.timinc.mc.cobblemon.counter.event

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.platform.events.PlatformEvents

object CounterEvents {
    fun register() {
        PlatformEvents.SERVER_STARTING.subscribe(Priority.LOWEST, ServerStartingHandler::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, CatchHandler::handle)
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, BattleFaintedHandler::handle)
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.LOWEST, FossilRevivedHandler::handle)
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, PokemonEntitySpawnHandler::handle)
    }
}
