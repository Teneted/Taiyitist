package org.bukkit.craftbukkit.v1_21_R5.util;

import net.minecraft.core.Holder;

public interface HolderHandleable<M> extends Handleable<M> {
   Holder<M> getHandleHolder();
}
