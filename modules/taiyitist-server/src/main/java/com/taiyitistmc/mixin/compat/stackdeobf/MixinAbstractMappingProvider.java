package com.taiyitistmc.mixin.compat.stackdeobf;

import com.taiyitistmc.util.I18n;
import dev.booky.stackdeobf.mappings.providers.AbstractMappingProvider;
import net.fabricmc.mappingio.MappingVisitor;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = AbstractMappingProvider.class, remap = false)
public abstract class MixinAbstractMappingProvider {

    @Shadow @Final protected String name;

    @Shadow protected abstract CompletableFuture<Void> downloadMappings0(Path path, Executor executor);

    @Shadow protected abstract CompletableFuture<Long> trackTime(CompletableFuture<Void> future);

    @Shadow protected abstract CompletableFuture<Void> parseMappings0(Executor executor);

    protected abstract CompletableFuture<Void> visitMappings0(net.fabricmc.mappingio.MappingVisitor par1, Executor par2);

    /**
     * @author wdog5
     * @reason i18n
     */
    @Overwrite
    private CompletableFuture<Void> downloadMappings(Path cacheDir, Executor executor) {
        LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.verifying"), this.name);
        return this.trackTime(this.downloadMappings0(cacheDir, executor)).thenAccept((timeDiff) -> {
            LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.verified"), this.name, timeDiff);
        });
    }

    /**
     * @author wdog5
     * @reason i18n
     */
    @Overwrite
    private CompletableFuture<Void> parseMappings(Executor executor) {
        LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.parsing"), this.name);
        return this.trackTime(this.parseMappings0(executor)).thenAccept((timeDiff) -> {
            LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.parsed"), this.name, timeDiff);
        });
    }

    /**
     * @author wdog5
     * @reason i18n
     */
    @Overwrite
    private CompletableFuture<Void> visitMappings(MappingVisitor visitor, Executor executor) {
        LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.caching"), this.name);
        return this.trackTime(this.visitMappings0(visitor, executor)).thenAccept((timeDiff) -> {
            LogManager.getLogger("StackDeobfuscator").info(I18n.as("stackdeobf.cached"), this.name, timeDiff);
        });
    }
}
