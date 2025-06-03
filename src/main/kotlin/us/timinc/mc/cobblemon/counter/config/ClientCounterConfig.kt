package us.timinc.mc.cobblemon.counter.config

import us.timinc.mc.cobblemon.counter.api.CounterTypeRegistry

class ClientCounterConfig {
    val broadcast: Set<String> = CounterTypeRegistry.types().toSet()
}