package us.timinc.mc.cobblemon.counter.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.counter.api.CounterType
import us.timinc.mc.cobblemon.counter.api.ScoreType
import us.timinc.mc.cobblemon.counter.extensions.getCounterManager

object GetScoreCommand : AbstractCommand() {
    override fun define(): LiteralArgumentBuilder<CommandSourceStack> =
        LiteralArgumentBuilder.literal<CommandSourceStack?>("get").executes(::execute)

    override fun run(
        ctx: CommandContext<CommandSourceStack>,
        player: ServerPlayer,
        counterType: CounterType,
        scoreType: ScoreType,
    ): Int {
        val manager = player.getCounterManager()
        val score = scoreType.getScore(manager, counterType)

        giveFeedback(
            Component.translatable(
                "cobbled_counter.command.feedback.get_score.${counterType.type}.${scoreType.type}",
                player.name,
                score
            ), ctx
        )

        return score
    }
}