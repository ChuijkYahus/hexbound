package coffee.cypher.hexbound.feature.construct.casting.action.command

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.evaluatable
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import coffee.cypher.hexbound.feature.construct.command.*
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.component.ConstructComponentKey
import coffee.cypher.hexbound.feature.construct.entity.component.InteractionComponent
import coffee.cypher.hexbound.feature.construct.entity.component.ItemHolderComponent
import coffee.cypher.hexbound.feature.construct.mishap.MishapMissingConstructComponent
import coffee.cypher.hexbound.feature.construct.mishap.MishapNoConstruct
import coffee.cypher.hexbound.util.mixinaccessor.construct
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.math.minus

abstract class OpGiveCommand : ConstMediaAction {
    override val argc: Int
        get() = baseArgc + 1

    protected abstract val baseArgc: Int

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        val construct = ctx.construct ?: throw MishapNoConstruct()
        val callback = evaluatable(args.last(), 0).map({ listOf(PatternIota(it)) }, { it.toList() })

        val command = getCommand(
            args,
            ctx,
            construct
        )

        construct.executeCommand(command, callback, ctx.world)

        return emptyList()
    }

    protected fun <T : Any> AbstractConstructEntity.requireComponent(key: ConstructComponentKey<T>): T {
        return getComponent(key) ?: throw MishapMissingConstructComponent(this, key)
    }

    protected abstract fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*>
}

object OpGiveCommandPickUp : OpGiveCommand() {
    override val baseArgc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val target = args.getItemEntity(0, argc)
        constructEntity.requireComponent(ItemHolderComponent)

        return PickUp(target.uuid)
    }
}

object OpGiveCommandDropOff : OpGiveCommand() {
    override val baseArgc = 0

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        constructEntity.requireComponent(ItemHolderComponent)

        return DropOff()
    }
}

object OpGiveCommandMoveTo : OpGiveCommand() {
    override val baseArgc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val pos = args.getVec3(0, argc)

        return MoveTo(pos)
    }
}

object OpGiveCommandHarvest : OpGiveCommand() {
    override val baseArgc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val pos = args.getBlockPos(0, argc)

        constructEntity.requireComponent(InteractionComponent)

        return Harvest(pos)
    }
}

object OpGiveCommandUseOnBlock : OpGiveCommand() {
    override val baseArgc = 2

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingEnvironment,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val pos = args.getBlockPos(0, argc)
        val side = args.getVec3(1, argc)

        val sideVec = if (side == Vec3d.ZERO) {
            constructEntity.pos - Vec3d.ofCenter(pos)
        } else {
            side
        }

        constructEntity.requireComponent(InteractionComponent)

        return UseItemOnBlock(pos, Direction.getFacing(sideVec.x, sideVec.y, sideVec.z))
    }
}
