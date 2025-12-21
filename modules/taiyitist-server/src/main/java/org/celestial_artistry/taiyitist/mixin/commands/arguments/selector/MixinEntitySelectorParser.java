package org.celestial_artistry.taiyitist.mixin.commands.arguments.selector;

import org.celestial_artistry.taiyitist.injection.commands.arguments.selector.InjectionEntitySelectorParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorParser implements InjectionEntitySelectorParser {

    @Shadow protected abstract void parseSelector() throws CommandSyntaxException;

    @Shadow public abstract EntitySelector parse() throws CommandSyntaxException;

    @Shadow private boolean usesSelectors;

    @Unique
    private AtomicBoolean taiyitist$overridePermissions = new AtomicBoolean(false);

    @Override
    public void parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        taiyitist$overridePermissions.set(overridePermissions);
        parseSelector();
    }

    @Redirect(method = "parseSelector", at = @At(value = "FIELD", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;usesSelectors:Z"))
    private void taiyitist$resetUseSelectors(EntitySelectorParser instance, boolean value) {
        this.usesSelectors = !taiyitist$overridePermissions.getAndSet(false);
    }

    @Redirect(method = "parse", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parseSelector()V"))
    private void taiyitist$resetParseSelectors(EntitySelectorParser instance) throws CommandSyntaxException {
        this.parseSelector(taiyitist$overridePermissions.getAndSet(false));
    }

    @Override
    public EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        taiyitist$overridePermissions.set(overridePermissions);
        return parse();
    }
}
