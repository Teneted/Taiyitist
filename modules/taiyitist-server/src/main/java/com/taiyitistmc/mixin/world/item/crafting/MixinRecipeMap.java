package com.taiyitistmc.mixin.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.asm.annotation.CreateConstructor;
import com.taiyitistmc.injection.world.item.crafting.InjectionRecipeMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(RecipeMap.class)
public class MixinRecipeMap implements InjectionRecipeMap {

    @Mutable
    @Shadow @Final
    public Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Mutable
    @Shadow @Final private Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey;

    @Mutable
    @Shadow @Final public static RecipeMap EMPTY;

    @CreateConstructor
    public void taiyitist$constructor(Multimap<RecipeType<?>, RecipeHolder<?>> multimap, LinkedHashMap<ResourceKey<Recipe<?>>, RecipeHolder<?>> map) {
        this.byType = multimap;
        this.byKey = map;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void taiyitist$setRecipes(Multimap multimap, Map map, CallbackInfo ci) {
        EMPTY = new RecipeMap(ImmutableMultimap.of(), Maps.newLinkedHashMap());
        byKey = new LinkedHashMap<ResourceKey<Recipe<?>>, RecipeHolder<?>>();
    }

    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private static RecipeMap taiyitist$resetRecipeMap(RecipeMap original, @Local ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> builder, @Local ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> builder2) {
        // CraftBukkit start - mutable, ordered
        return new RecipeMap(LinkedHashMultimap.create(builder.build()), Maps.newLinkedHashMap(builder2.build()));
    }

    @Override
    public void addRecipe(RecipeHolder<?> irecipe) {
        Collection<RecipeHolder<?>> map = this.byType.get(irecipe.value().getType());

        if (byKey.containsKey(irecipe.id())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + irecipe.id());
        } else {
            map.add(irecipe);
            ((LinkedHashMap) byKey).putFirst(irecipe.id(), irecipe); // CraftBukkit - ordered
        }
    }

    @Override
    public boolean removeRecipe(ResourceKey<Recipe<?>> mcKey) {
        boolean removed = false;
        Iterator<RecipeHolder<?>> iter = byType.values().iterator();
        while (iter.hasNext()) {
            RecipeHolder<?> recipe = iter.next();
            if (recipe.id().equals(mcKey)) {
                iter.remove();
                removed = true;
            }
        }
        removed |= byKey.remove(mcKey) != null;

        return removed;
    }
    // CraftBukkit end
}
