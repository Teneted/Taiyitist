package com.taiyitistmc.mixin.util.worldupdate;

import net.minecraft.util.worldupdate.WorldUpgrader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(targets = "net.minecraft.util.worldupdate.WorldUpgrader$ChunkUpgrader")
public class MixinWorldUpgrader_ChunkUpgrader {

    @Shadow @Final
    WorldUpgrader field_48738;
}
