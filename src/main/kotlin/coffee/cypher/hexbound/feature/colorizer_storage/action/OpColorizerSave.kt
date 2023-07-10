package coffee.cypher.hexbound.feature.colorizer_storage.action

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents
import coffee.cypher.hexbound.feature.colorizer_storage.mishap.MishapColorizerNotSet
import coffee.cypher.hexbound.feature.colorizer_storage.mishap.MishapTooManyColorizers
import coffee.cypher.hexbound.init.memorizedColorizers
import coffee.cypher.hexbound.util.nonBlankSignature

object OpColorizerSave : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingEnvironment
    ): SpellAction.Result {
        val pattern = args.getPattern(0, 1)

        val currentColorizer = HexCardinalComponents.FAVORED_PIGMENT[ctx.caster].pigment

        if (ctx.caster.memorizedColorizers.size >= 64 && pattern.nonBlankSignature !in ctx.caster.memorizedColorizers) {
            throw MishapTooManyColorizers()
        }

        if (currentColorizer == FrozenPigment.DEFAULT.get()) {
            throw MishapColorizerNotSet()
        }

        return SpellAction.Result(
            Spell(pattern, currentColorizer),
            0,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern, val value: FrozenPigment) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            ctx.world.getBlockState(BlockPos(0, 0, 0)).isAir
            ctx.caster.memorizedColorizers[key.nonBlankSignature] = value
        }
    }
}
