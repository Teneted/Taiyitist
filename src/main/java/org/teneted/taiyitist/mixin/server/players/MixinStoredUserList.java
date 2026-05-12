package org.teneted.taiyitist.mixin.server.players;

import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.players.InjectionStoredUserList;

@Mixin(StoredUserList.class)
public class MixinStoredUserList<K, V extends StoredUserEntry<K>> implements InjectionStoredUserList<K, V> {
}
