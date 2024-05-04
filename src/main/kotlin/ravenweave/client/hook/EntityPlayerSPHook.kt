package ravenweave.client.hook

import net.weavemc.api.Hook
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import ravenweave.client.generateForwarderMethod
import java.nio.file.Files
import kotlin.io.path.Path

internal class EntityPlayerSPHook : Hook("net/minecraft/client/entity/EntityPlayerSP") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.generateForwarderMethod(
            "pushOutOfBlocks",
            "sendPlayerAbilities",
            "isSneaking",
        )

        cfg.computeFrames()
    }
}