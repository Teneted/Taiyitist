package com.taiyitistmc.mixin.server.level;

import com.taiyitistmc.injection.server.level.InjectionChunkHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder implements InjectionChunkHolder {

    @Shadow private int oldTicketLevel;

    @Override
    public LevelChunk getFullChunkNow() {
        // Note: We use the oldTicketLevel for isLoaded checks.
        if (!ChunkLevel.fullStatus(this.oldTicketLevel).isOrAfter(FullChunkStatus.FULL)) return null;
        return this.getFullChunkNowUnchecked();
    }

    @Override
    public LevelChunk getFullChunkNowUnchecked() {
        return (LevelChunk) ((ChunkHolder) (Object) this).getChunkIfPresentUnchecked(ChunkStatus.FULL);
    }
}
