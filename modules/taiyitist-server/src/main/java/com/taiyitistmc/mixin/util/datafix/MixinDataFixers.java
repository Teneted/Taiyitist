package com.taiyitistmc.mixin.util.datafix;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;
import net.minecraft.util.datafix.fixes.EntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.References;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DataFixers.class)
public class MixinDataFixers {

    @Inject(method = "addFixers",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/datafixers/DataFixerBuilder;addSchema(ILjava/util/function/BiFunction;)Lcom/mojang/datafixers/schemas/Schema;",
                    remap = false,
                    ordinal = 38))
    private static void taiyitist$addFix(DataFixerBuilder builder, CallbackInfo ci,
                                         @Local(ordinal = 44) Schema schema45) {
        // CraftBukkit start
        builder.addFixer(new com.mojang.datafixers.DataFix(schema45, false) {
            @Override
            protected com.mojang.datafixers.TypeRewriteRule makeRule() {
                return this.fixTypeEverywhereTyped("Player CustomName", this.getInputSchema().getType(References.PLAYER), (typed) -> {
                    return typed.update(DSL.remainderFinder(), (dynamic) -> {
                        String s = dynamic.get("CustomName").asString("");

                        return s.isEmpty() ? dynamic.remove("CustomName") : dynamic.set("CustomName", LegacyComponentDataFixUtils.createPlainTextComponent(dynamic.getOps(), s));
                    });
                });
            }
        });
        // CraftBukkit end
    }
}
