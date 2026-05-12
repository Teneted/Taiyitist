package org.teneted.taiyitist.mixin.commands.arguments.selector;

import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.commands.arguments.selector.InjectionEntitySelectorParser;

@Mixin(EntitySelectorParser.class)
public class MixinEntitySelectorParser implements InjectionEntitySelectorParser {
}
