package us.timinc.mc.cobblemon.counter.api

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.api.storage.player.InstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.getPlayer
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.counter.CounterMod
import us.timinc.mc.cobblemon.counter.storage.PlayerInstancedDataStores
import java.util.*

class CounterManager(
    override val uuid: UUID,
    override val counters: Map<CounterType, Counter> = CounterRegistry.counterTypes.associateWith { Counter() },
) : AbstractCounterManager(), InstancedPlayerData {
    companion object {
        val CODEC: Codec<CounterManager> = RecordCodecBuilder.create { instance ->
            instance.group(PrimitiveCodec.STRING.fieldOf("uuid").forGetter { it.uuid.toString() },
                Codec.unboundedMap(PrimitiveCodec.STRING, Counter.CODEC).fieldOf("counters").forGetter { manager ->
                    manager.counters.keys.map { key -> key.type }
                        .associateWith { key -> manager.counters[CounterType.entries.find { it.type == key }]!!.clone() }
                }).apply(instance) { uuid, counters ->
                CounterManager(UUID.fromString(uuid), counters.keys.map { key ->
                    CounterType.entries.find { it.type == key }!!
                }.associateWith { key -> counters[key.type]!!.clone() })
            }
        }
    }

    fun getCounter(counterType: CounterType): Counter {
        return counters[counterType]
            ?: throw Error("${counterType.type} was not registered with the CounterRegistry before ")
    }

    fun record(speciesId: ResourceLocation, formName: String, counterType: CounterType) {
        val counter = getCounter(counterType)
        val speciesRecord = counter.count.getOrPut(speciesId) { mutableMapOf() }
        speciesRecord[formName] = speciesRecord.getOrDefault(formName, 0) + 1
        counter.streak.add(speciesId, formName, CounterMod.config.breakStreakOnForm.contains(counterType.type))

        uuid.getPlayer()?.sendPacket(
            SetClientPlayerDataPacket(
                type = PlayerInstancedDataStores.COUNTER,
                playerData = toClientData(),
                isIncremental = true
            )
        )
    }

    override fun toClientData(): ClientCounterManager {
        val cloned: MutableMap<CounterType, Counter> = mutableMapOf()
        counters.forEach { (type, counter) -> cloned[type] = counter.clone() }
        return ClientCounterManager(cloned)
    }

    fun record(pokemon: Pokemon, counterType: CounterType) {
        record(pokemon.species.resourceIdentifier, pokemon.form.name, counterType)
    }

    fun getStreak(counterType: CounterType): Streak {
        return getCounter(counterType).streak
    }

    fun getStreakCount(counterType: CounterType, species: ResourceLocation? = null, form: String? = null): Int {
        val streak = getStreak(counterType)
        if (species === null) return streak.count
        if (form === null) return if (species == streak.species) streak.count else 0
        return if (species == streak.species && form == streak.form) streak.count else 0
    }

    fun getCount(counterType: CounterType, species: ResourceLocation? = null, form: String? = null): Int {
        val speciesRecord = getCounter(counterType).count[species] ?: return 0
        if (form === null) return speciesRecord.values.fold(0) { acc, element -> acc + element }
        return speciesRecord.getOrDefault(form, 0)
    }
}