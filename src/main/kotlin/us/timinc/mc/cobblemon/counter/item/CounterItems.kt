@file:Suppress("MemberVisibilityCanBePrivate")

package us.timinc.mc.cobblemon.counter.item

import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item.Properties
import us.timinc.mc.cobblemon.counter.CounterMod

object CounterItems {
    val COUNTER_KEY = ResourceKey.create(Registries.ITEM, CounterMod.modResource("counter"))
    val COUNTER =
        Registry.register(BuiltInRegistries.ITEM, COUNTER_KEY.location(), CounterItem(Properties().stacksTo(1)))

    fun register() {
        ItemGroupEvents.modifyEntriesEvent(CobblemonItemGroups.UTILITY_ITEMS_KEY).register {
            it.accept(COUNTER)
        }
    }
}