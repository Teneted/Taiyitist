package org.teneted.taiyitist.mixin.commands;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teneted.taiyitist.injection.commands.InjectionCommandNode;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(value = CommandNode.class, remap = false)
public class MixinCommandNode<S> implements InjectionCommandNode {

    @Shadow
    @Final
    private Map<String, CommandNode<S>> children;

    @Shadow
    @Final
    private Map<String, LiteralCommandNode<S>> literals;

    @Shadow
    @Final
    private Map<String, ArgumentCommandNode<S, ?>> arguments;

    @Shadow
    @Final
    private Predicate<S> requirement;

    @Override
    public void removeCommand(String name) {
        children.remove(name);
        literals.remove(name);
        arguments.remove(name);
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void taiyitist$canUse(S source, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (source instanceof CommandSourceStack) {
            try {
                ((CommandSourceStack) source).taiyitist$setCurrentCommand(((CommandNode<?>) (Object) this));
                cir.setReturnValue(requirement.test(source));
            } finally {
                ((CommandSourceStack) source).taiyitist$setCurrentCommand(null);
            }
            // CraftBukkit end
        }
    }
}
