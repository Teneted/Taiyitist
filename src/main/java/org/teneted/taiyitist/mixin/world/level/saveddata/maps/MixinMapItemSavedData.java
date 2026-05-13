package org.teneted.taiyitist.mixin.world.level.saveddata.maps;

import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.world.level.saveddata.maps.InjectionMapItemSavedData;

@Mixin(MapItemSavedData.class)
public class MixinMapItemSavedData implements InjectionMapItemSavedData {
}
