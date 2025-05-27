package us.timinc.mc.cobblemon.counter.event.handler

import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent
import us.timinc.mc.cobblemon.counter.api.CounterType
import us.timinc.mc.cobblemon.counter.extensions.record

object EggHatchHandler {
    fun handle(evt: HatchEggEvent.Post) {
        val player = evt.player
        val pokemon = evt.egg.create(player)
        player.record(pokemon, CounterType.HATCH)
    }
}