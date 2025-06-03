package us.timinc.mc.cobblemon.counter.api

import net.minecraft.resources.ResourceLocation

abstract class ScoreType(val type: String) {
    abstract fun getScore(
        manager: CounterManager,
        counterType: CounterType,
        species: ResourceLocation? = null,
        formName: String? = null,
    ): Int

    abstract fun setScore(
        manager: CounterManager,
        counterType: CounterType,
        species: ResourceLocation,
        formName: String,
        score: Int,
    )
}