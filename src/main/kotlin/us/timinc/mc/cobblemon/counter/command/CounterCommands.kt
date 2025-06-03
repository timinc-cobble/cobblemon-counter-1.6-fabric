package us.timinc.mc.cobblemon.counter.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.synchronization.SingletonArgumentInfo
import us.timinc.mc.cobblemon.counter.CounterMod.modResource
import us.timinc.mc.cobblemon.counter.command.argument.CounterTypeArgument
import us.timinc.mc.cobblemon.counter.command.argument.ScoreTypeArgument

object CounterCommands {
    fun register() {
        ArgumentTypeRegistry.registerArgumentType(
            modResource("counter_type"),
            CounterTypeArgument::class.java,
            SingletonArgumentInfo.contextFree(CounterTypeArgument::type)
        )
        ArgumentTypeRegistry.registerArgumentType(
            modResource("score_type"),
            ScoreTypeArgument::class.java,
            SingletonArgumentInfo.contextFree(ScoreTypeArgument::type)
        )

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            registerOneWithPlayerAndType(dispatcher, GetScoreCommand.define())
            registerOneWithPlayerAndType(dispatcher, SetScoreCommand.define())
        }
    }

    private fun registerOneWithPlayerAndType(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        command: LiteralArgumentBuilder<CommandSourceStack>,
    ) {
        dispatcher.register(
            Commands.literal("count").then(
                Commands.argument("player", EntityArgument.player()).then(
                        Commands.argument("counterType", CounterTypeArgument.type()).then(
                                Commands.argument("scoreType", ScoreTypeArgument.type()).then(command)
                            )
                    )
            )
        )
    }
}