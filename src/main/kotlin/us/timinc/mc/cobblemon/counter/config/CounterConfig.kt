package us.timinc.mc.cobblemon.counter.config

import us.timinc.mc.cobblemon.counter.api.CounterType

class CounterConfig {
    val debug: Boolean = false
    val breakStreakOnForm: List<String> = CounterType.entries.map { it.type }
}