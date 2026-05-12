package org.teneted.taiyitist.mixin.server.players;

import net.minecraft.server.players.BanListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.players.InjectionBanListEntry;

@Mixin(BanListEntry.class)
public class MixinBanListEntry implements InjectionBanListEntry {
}
