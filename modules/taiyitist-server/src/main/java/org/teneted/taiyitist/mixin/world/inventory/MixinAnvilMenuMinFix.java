package org.teneted.taiyitist.mixin.world.inventory;

import org.teneted.taiyitist.config.TaiyitistConfig;
import org.teneted.taiyitist.injection.world.inventory.InjectionAnvilMenu;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilMenu.class)
public class MixinAnvilMenuMinFix implements InjectionAnvilMenu {

    @Unique
    public int maximumRepairCost = Math.min(Short.MAX_VALUE, Math.max(41, TaiyitistConfig.maximumRepairCost));

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int taiyitist$maxRepairCost(int constant) {
        return constant - 40 + maximumRepairCost;
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 39), require = 0)
    private int taiyitist$maximumRepairCost2(int i) {
        return i - 40 + maximumRepairCost;
    }

    @Override
    public int bridge$maximumRepairCost() {
        return maximumRepairCost;
    }

    @Override
    public void taiyitist$setMaximumRepairCost(int maximumRepairCost) {
        this.maximumRepairCost = maximumRepairCost;
    }
}
