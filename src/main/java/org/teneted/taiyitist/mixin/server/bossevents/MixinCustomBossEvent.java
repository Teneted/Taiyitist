package org.teneted.taiyitist.mixin.server.bossevents;

import net.minecraft.server.bossevents.CustomBossEvent;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.craftbukkit.boss.CraftKeyedBossbar;
import org.spongepowered.asm.mixin.Mixin;
import org.teneted.taiyitist.injection.server.bossevents.InjectionCustomBossEvent;

@Mixin(CustomBossEvent.class)
public class MixinCustomBossEvent implements InjectionCustomBossEvent {

    // CraftBukkit start
    private KeyedBossBar bossBar;

    @Override
    public KeyedBossBar getBukkitEntity() {
        if (bossBar == null) {
            bossBar = new CraftKeyedBossbar(((CustomBossEvent) (Object) this));
        }
        return bossBar;
    }
}
