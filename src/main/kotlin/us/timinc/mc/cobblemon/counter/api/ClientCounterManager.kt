package us.timinc.mc.cobblemon.counter.api

import com.cobblemon.mod.common.api.storage.player.client.ClientInstancedPlayerData
import com.cobblemon.mod.common.net.messages.client.SetClientPlayerDataPacket
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import net.minecraft.network.RegistryFriendlyByteBuf
import us.timinc.mc.cobblemon.counter.CounterModClient
import us.timinc.mc.cobblemon.counter.storage.PlayerInstancedDataStores

class ClientCounterManager(
    override val counters: MutableMap<CounterType, Counter>
) : AbstractCounterManager(), ClientInstancedPlayerData {
    override fun encode(buf: RegistryFriendlyByteBuf) {
        buf.writeMap(
            counters,
            { _, key -> buf.writeString(key.type) },
            { _, value -> value.encode(buf) }
        )
    }

    companion object {
        fun decode(buf: RegistryFriendlyByteBuf): SetClientPlayerDataPacket {
            val map = buf.readMap(
                { buf.readString().let { type -> CounterType.entries.find { it.type == type }!! } },
                { Counter().also { it.decode(buf) } }
            )
            return SetClientPlayerDataPacket(PlayerInstancedDataStores.COUNTER, ClientCounterManager(map))
        }

        fun runAction(data: ClientInstancedPlayerData) {
            if (data !is ClientCounterManager) return
            CounterModClient.clientCounterData = data
        }

        fun runActionIncremental(data: ClientInstancedPlayerData) {
            if (data !is ClientCounterManager) return

            val clientData = CounterModClient.clientCounterData
            for ((counterType, counter) in data.counters.entries) {
                val targetClientCounter = clientData.counters[counterType]
                    ?: throw Error("Unregistered counter type sent by server ${counterType.type}")
                for ((speciesId, speciesRecord) in counter.count) {
                    val clientSpeciesRecord = targetClientCounter.count.getOrPut(speciesId, ::mutableMapOf)
                    for ((formName, count) in speciesRecord) {

                        clientSpeciesRecord[formName] = count
                    }
                }
                targetClientCounter.streak = counter.streak
            }
        }
    }
}