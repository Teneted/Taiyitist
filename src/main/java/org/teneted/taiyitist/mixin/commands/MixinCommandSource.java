package org.teneted.taiyitist.mixin.commands;

import net.minecraft.commands.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.InjectionCommandSource;

@Mixin(CommandSource.class)
public class MixinCommandSource implements InjectionCommandSource {
}
