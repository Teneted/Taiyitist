package org.teneted.taiyitist.mixin.commands.arguments.selector;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teneted.taiyitist.injection.commands.arguments.selector.InjectionEntitySelectorParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorParser implements InjectionEntitySelectorParser {

    @Shadow
    private boolean usesSelectors;

    @Shadow
    private int startPosition;

    @Shadow
    @Final
    private StringReader reader;

    @Shadow
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;

    @Shadow
    protected abstract CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> names);

    @Shadow
    @Final
    private boolean allowSelectors;

    @Shadow
    @Final
    public static SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED;

    @Shadow
    protected abstract void parseNameOrUUID() throws CommandSyntaxException;

    @Shadow
    protected abstract void finalizePredicates();

    @Shadow
    public abstract EntitySelector getSelector();

    @Shadow
    protected abstract void parseSelector() throws CommandSyntaxException;

    @Override
    public void parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        this.usesSelectors = !overridePermissions;
        this.parseSelector();
        // CraftBukkit end
    }

    @Override
    public EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::suggestNameOrSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.allowSelectors) {
                throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector(overridePermissions);
        } else {
            this.parseNameOrUUID();
        }

        this.finalizePredicates();
        return this.getSelector();
    }
}
