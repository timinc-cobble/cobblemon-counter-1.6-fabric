package us.timinc.mc.cobblemon.counter.api

abstract class AbstractCounterManager {
    abstract val counters: Map<CounterType, Counter>
}