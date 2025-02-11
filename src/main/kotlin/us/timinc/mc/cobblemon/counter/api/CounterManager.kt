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

    fun breakStreak(counterType: CounterType) {
        val counter = getCounter(counterType)
        counter.streak = Streak()
    }

    fun record(initialSpeciesId: ResourceLocation, initialFormName: String, counterType: CounterType) {
        val formOverride = CounterMod.config.getFormOverride(initialSpeciesId, initialFormName)
        val speciesId = if (formOverride === null) initialSpeciesId else ResourceLocation.parse(formOverride.toSpecies)
        val formName = if (formOverride === null) initialFormName else formOverride.toForm

        val counter = getCounter(counterType)
        val speciesRecord = counter.count.getOrPut(speciesId) { mutableMapOf() }
        speciesRecord[formName] = speciesRecord.getOrDefault(formName, 0) + 1
        counter.streak.add(speciesId, formName, CounterMod.config.breakStreakOnForm.contains(counterType.type))

        val player = uuid.getPlayer() ?: return

        val patchData = ClientCounterManager(
            mutableMapOf(
                counterType to Counter(
                    mutableMapOf(
                        speciesId to mutableMapOf(formName to speciesRecord[formName]!!)
                    ),
                    counter.streak
                )
            )
        )

        player.sendPacket(
            SetClientPlayerDataPacket(
                type = PlayerInstancedDataStores.COUNTER,
                playerData = patchData,
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
}