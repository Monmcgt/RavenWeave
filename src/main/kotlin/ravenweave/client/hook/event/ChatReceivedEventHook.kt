package ravenweave.client.hook.event

import net.weavemc.api.Hook
import net.weavemc.api.event.CancellableEvent
import net.weavemc.internals.asm
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import ravenweave.client.callEvent
import ravenweave.client.event.ChatReceivedEvent
import ravenweave.client.internalNameOf
import ravenweave.client.named

internal class ChatReceivedEventHook : Hook("net/minecraft/client/gui/GuiNewChat") {
    override fun transform(node: ClassNode, cfg: AssemblerConfig) {
        node.methods.named("printChatMessageWithOptionalDeletion").instructions.insert(asm {
            new(internalNameOf<ChatReceivedEvent>())
            dup
            dup
            aload(1)
            invokespecial(
                internalNameOf<ChatReceivedEvent>(),
                "<init>",
                "(Lnet/minecraft/util/IChatComponent;)V"
            )
            callEvent()

            val end = LabelNode()

            invokevirtual(internalNameOf<CancellableEvent>(), "isCancelled", "()Z")
            ifeq(end)

            _return

            +end
            f_same()
        })
    }
}
