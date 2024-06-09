package gay.`object`.hexbound.feature.construct.broadcasting

import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.xplat.IXplatAbstractions
import gay.`object`.hexbound.feature.construct.entity.AbstractConstructEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

data class BroadcastingContext(
    val broadcaster: BlockPos,
    val center: Vec3d,
    val radius: Double,
    val pattern: HexPattern?,
    val particleCenter: Vec3d,
    val particleOffset: Double
) {
    fun broadcast(instructions: List<Iota>, ctx: CastingContext) {
        val radiusSqr = radius * radius

        ctx.world.getEntitiesByClass(AbstractConstructEntity::class.java, Box(center, center).expand(radius)) {
            it.pos.squaredDistanceTo(center) <= radiusSqr
        }.forEach {
            it.acceptInstructions(instructions, ctx.caster, true, pattern)
        }

        val random = ctx.world.random

        val particleColorizer = IXplatAbstractions.INSTANCE.getColorizer(ctx.caster)
        val particleColor = particleColorizer.getColor(
            random.nextFloat() * 16384,
            Vec3d(
                random.nextFloat().toDouble(),
                random.nextFloat().toDouble(),
                random.nextFloat().toDouble()
            ).multiply((random.nextFloat() * 3).toDouble())
        )

        ConstructBroadcasterBlock.onActivated(ctx.world, broadcaster)

        BroadcasterActivatedS2CPacket(particleCenter, particleOffset, particleColor).send(ctx.world)
    }
}
