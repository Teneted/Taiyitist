package com.taiyitistmc.mixin.world.item.crafting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.asm.annotation.TransformAccess;
import com.taiyitistmc.injection.world.item.crafting.InjectionIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(Ingredient.class)
public class MixinIngredient implements InjectionIngredient {

    // CraftBukkit start
    @Nullable
    private List<ItemStack> itemStacks;

    public boolean isExact() {
        return this.itemStacks != null;
    }

    @Override
    public boolean bridge$exact() {
        return isExact();
    }

    @Override
    public List<ItemStack> itemStacks() {
        return this.itemStacks;
    }

    @Override
    public void taiyitist$setItemStacks(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static Ingredient ofStacks(List<ItemStack> stacks) {
        Ingredient recipe = Ingredient.of(stacks.stream().map(ItemStack::getItem));
        recipe.taiyitist$setItemStacks(stacks);
        return recipe;
    }
    // CraftBukkit end

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void taiyitist$checkTest(ItemStack itemstack, CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit start
        if (this.isExact()) {
            for (ItemStack itemstack1 : this.itemStacks()) {
                if (itemstack1.getItem() == itemstack.getItem() && ItemStack.isSameItemSameComponents(itemstack, itemstack1)) {
                    cir.setReturnValue(true);
                }
            }

            cir.setReturnValue(false);
        }
        // CraftBukkit end
    }

    @ModifyExpressionValue(method = "equals", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private boolean taiyitist$addCheck(boolean original, @Local(argsOnly = true) Object object) {
        var recipeitemstack = ((Ingredient) object);
        return original && Objects.equals(this.itemStacks, recipeitemstack.itemStacks()); // CraftBukkit
    }
}
