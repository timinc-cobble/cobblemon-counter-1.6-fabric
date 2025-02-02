package us.timinc.mc.cobblemon.counter.event

import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent
import us.timinc.mc.cobblemon.counter.api.CounterType
import us.timinc.mc.cobblemon.counter.extensions.record

object FossilRevivedHandler {
    fun handle(fossilRevivedEvent: FossilRevivedEvent) {
        val pokemon = fossilRevivedEvent.pokemon
        val player = fossilRevivedEvent.player ?: return
        player.record(pokemon, CounterType.RESURRECTION)
    }
}
