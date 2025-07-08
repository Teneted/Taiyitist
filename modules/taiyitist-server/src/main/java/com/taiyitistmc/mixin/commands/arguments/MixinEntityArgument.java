package com.taiyitistmc.mixin.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.taiyitistmc.injection.commands.arguments.InjectionEntityArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityArgument.class)
public class MixinEntityArgument implements InjectionEntityArgument {

    @Shadow @Final
    boolean single;

    @Shadow @Final public static SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER;

    @Shadow @Final
    boolean playersOnly;

    @Shadow @Final public static SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY;

    @Shadow @Final public static SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED;

    @Override
    public EntitySelector parse(StringReader stringReader, boolean bl, boolean overridePermissions) throws CommandSyntaxException {
        int i = 0;
        EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader, bl);
        EntitySelector entitySelector = entitySelectorParser.parse(overridePermissions);
        if (entitySelector.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw ERROR_NOT_SINGLE_PLAYER.createWithContext(stringReader);
            } else {
                stringReader.setCursor(0);
                throw ERROR_NOT_SINGLE_ENTITY.createWithContext(stringReader);
            }
        } else if (entitySelector.includesEntities() && this.playersOnly && !entitySelector.isSelfSelector()) {
            stringReader.setCursor(0);
            throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(stringReader);
        } else {
            return entitySelector;
        }
    }
}
