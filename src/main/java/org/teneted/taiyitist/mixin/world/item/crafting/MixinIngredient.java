package org.teneted.taiyitist.mixin.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.asm.annotation.TransformAccess;
import org.teneted.taiyitist.injection.world.item.crafting.InjectionIngredient;

import java.util.List;

@Mixin(Ingredient.class)
public class MixinIngredient implements InjectionIngredient {

    // CraftBukkit start
    @Nullable
    private List<ItemStack> itemStacks;

    public boolean isExact() {
        return this.itemStacks != null;
    }

    @Override
    public List<ItemStack> itemStacks() {
        return this.itemStacks;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static Ingredient ofStacks(List<ItemStack> stacks) {
        Ingredient recipe = Ingredient.of(stacks.stream().map(ItemStack::getItem));
        recipe.taiyitist$setItemStacks(stacks);
        return recipe;
    }
    // CraftBukkit end


    @Override
    public boolean bridge$exact() {
        return isExact();
    }

    @Override
    public void taiyitist$setItemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }
}
