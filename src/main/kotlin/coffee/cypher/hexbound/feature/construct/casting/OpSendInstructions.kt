package coffee.cypher.hexbound.feature.construct.casting

import at.petrak.hexcasting.api.spell.*
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.util.getConstruct

object OpSendInstructions : SpellAction {
    override val argc = 2

    override fun execute(
        args: List<Iota>,
        ctx: CastingContext
    ): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val construct = args.getConstruct(0, argc)
        val instructions = args.getList(1, argc)

        ctx.assertEntityInRange(construct)

        return Triple(
            Spell(construct, instructions.toList()),
            0,
            emptyList()
        )
    }

    private class Spell(
        val constructEntity: AbstractConstructEntity<*>,
        val instructions: List<Iota>
    ) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            constructEntity.instructionSet = instructions
        }
    }
}
