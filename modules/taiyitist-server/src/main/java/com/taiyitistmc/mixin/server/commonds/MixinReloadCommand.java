package com.taiyitistmc.mixin.server.commonds;

import com.taiyitistmc.asm.annotation.TransformAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;

@Mixin(ReloadCommand.class)
public abstract class MixinReloadCommand {

    @Shadow
    public static Collection<String> discoverNewPacks(PackRepository packRepository, WorldData worldData, Collection<String> collection) {
        return null;
    }

    // CraftBukkit start
    @TransformAccess(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC)
    private static void reload(MinecraftServer minecraftserver) {
        PackRepository resourcepackrepository = minecraftserver.getPackRepository();
        WorldData savedata = minecraftserver.getWorldData();
        Collection<String> collection = resourcepackrepository.getSelectedIds();
        Collection<String> collection1 = discoverNewPacks(resourcepackrepository, savedata, collection);
        minecraftserver.reloadResources(collection1);
    }
    // CraftBukkit end
}
