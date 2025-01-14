package gay.`object`.hexbound.feature.construct.command.execution

import gay.`object`.hexbound.feature.construct.command.exception.MissingComponentConstructCommandException
import gay.`object`.hexbound.feature.construct.entity.AbstractConstructEntity
import gay.`object`.hexbound.feature.construct.entity.component.ConstructComponentKey
import net.minecraft.server.world.ServerWorld

class ConstructCommandContext(val construct: AbstractConstructEntity, val world: ServerWorld) {
    internal val requirements = mutableListOf<() -> Unit>()

    fun <T : Any> requireComponent(key: ConstructComponentKey<T>): T {
        return construct.getComponent(key) ?: throw MissingComponentConstructCommandException(key)
    }

    fun maintain(condition: () -> Unit) {
        condition()

        requirements += condition
    }
}
