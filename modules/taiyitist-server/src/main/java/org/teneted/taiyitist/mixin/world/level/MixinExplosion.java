package org.teneted.taiyitist.mixin.world.level;

import com.google.common.collect.Sets;
import org.teneted.taiyitist.injection.world.level.InjectionExplosion;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Explosion.class, priority = 900)
public abstract class MixinExplosion implements InjectionExplosion {

    // @formatter:off
    @Shadow @Final private Level level;
    @Shadow @Final private Explosion.BlockInteraction blockInteraction;
    @Shadow @Mutable @Final private float radius;
    @Shadow @Final private ObjectArrayList<BlockPos> toBlow;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final public Entity source;
    @Shadow public abstract DamageSource getDamageSource();
    @Shadow @Final private Map<Player, Vec3> hitPlayers;
    @Shadow @Final private boolean fire;
    @Shadow @Final private RandomSource random;
    @Shadow private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> dropPositionArray, ItemStack stack, BlockPos pos) { }
    @Shadow @Final private ExplosionDamageCalculator damageCalculator;
    @Shadow public abstract boolean interactsWithBlocks();
    @Shadow @Nullable public abstract LivingEntity getIndirectSourceEntity();
    @Shadow public static float getSeenPercent(Vec3 p_46065_, Entity p_46066_) { return 0f; }
    // @formatter:on

    @Shadow public abstract void clearToBlow();

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)V",
            at = @At("RETURN"))
    public void taiyitist$adjustSize(Level worldIn, Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Explosion.BlockInteraction modeIn, CallbackInfo ci) {
        this.radius = Math.max(sizeIn, 0F);
    }

    @Unique
    public boolean wasCanceled = false; // CraftBukkit - add field

    public float yield;

    /**
     * @author wdog5
     * @reason
     */
    @Overwrite
    public void finalizeExplosion(boolean spawnParticles) {
        if (wasCanceled) {
            return;
        }
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        }

        boolean flag = this.interactsWithBlocks();

        if (spawnParticles) {
            if (!(this.radius < 2.0F) && flag) {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            } else {
                this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
            }
        }

        if (flag) {
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            boolean flag2 = this.getIndirectSourceEntity() instanceof Player;
            Util.shuffle(this.toBlow, this.level.random);

            {
                // CraftBukkit start
                org.bukkit.World bworld = this.level.getWorld();
                org.bukkit.entity.Entity explode = this.source == null ? null : this.source.getBukkitEntity();
                Location location = new Location(bworld, this.x, this.y, this.z);

                List<org.bukkit.block.Block> blockList = new ObjectArrayList<>();
                for (int i1 = this.toBlow.size() - 1; i1 >= 0; i1--) {
                    BlockPos cpos = this.toBlow.get(i1);
                    org.bukkit.block.Block bblock = CraftBlock.at(this.level, cpos);
                    if (!bblock.getType().isAir()) {
                        blockList.add(bblock);
                    }
                }

                boolean cancelled;
                List<org.bukkit.block.Block> bukkitBlocks;

                if (explode != null) {
                    EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F);
                    this.level.getCraftServer().getPluginManager().callEvent(event);
                    cancelled = event.isCancelled();
                    bukkitBlocks = event.blockList();
                    yield = event.getYield();
                } else {
                    BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY ? 1.0F / this.radius : 1.0F);
                    this.level.getCraftServer().getPluginManager().callEvent(event);
                    cancelled = event.isCancelled();
                    bukkitBlocks = event.blockList();
                    yield = event.getYield();
                }

                this.toBlow.clear();
                for (org.bukkit.block.Block bblock : bukkitBlocks) {
                    BlockPos coords = new BlockPos(bblock.getX(), bblock.getY(), bblock.getZ());
                    toBlow.add(coords);
                }

                if (cancelled) {
                    this.wasCanceled = true;
                    return;
                }
                // CraftBukkit end
            }

            ObjectListIterator var5 = this.toBlow.iterator();

            while(var5.hasNext()) {
                BlockPos blockpos = (BlockPos)var5.next();
                BlockState blockstate = this.level.getBlockState(blockpos);
                Block block = blockstate.getBlock();

                // CraftBukkit start - TNTPrimeEvent
                if (block instanceof TntBlock) {
                    Entity sourceEntity = source == null ? null : source;
                    BlockPos sourceBlock = sourceEntity == null ? BlockPos.containing(this.x, this.y, this.z) : null;
                    CraftEventFactory.finalizeExplosion = true; // Banner
                    if (!CraftEventFactory.callTNTPrimeEvent(this.level, blockpos, TNTPrimeEvent.PrimeCause.EXPLOSION, sourceEntity, sourceBlock)) {
                        continue;
                    }
                }
                // CraftBukkit end
                if (!blockstate.isAir()) {
                    BlockPos blockpos1 = blockpos.immutable();
                    this.level.getProfiler().push("explosion_blocks");
                    if (block.dropFromExplosion(((Explosion) (Object) this))&& this.level instanceof ServerLevel serverLevel) {
                        BlockEntity blockEntity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
                        LootParams.Builder builder = (new LootParams.Builder(serverLevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                        if (yield < 1.0F) { // CraftBukkit - add yield
                            builder.withParameter(LootContextParams.EXPLOSION_RADIUS, 1.0F / yield); // CraftBukkit - add yield
                        }

                        blockstate.spawnAfterBreak(serverLevel, blockpos, ItemStack.EMPTY, flag2);
                        blockstate.getDrops(builder).forEach((itemStack) -> {
                            addBlockDrops(objectArrayList, itemStack, blockpos1);
                        });

                    }

                    this.level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 3);
                    block.wasExploded(this.level, blockpos, ((Explosion) (Object) this));
                    this.level.getProfiler().pop();
                }
            }

            var5 = objectArrayList.iterator();

            while(var5.hasNext()) {
                Pair<ItemStack, BlockPos> pair = (Pair)var5.next();
                Block.popResource(this.level, pair.getSecond(), pair.getFirst());
            }
        }

        if (this.fire) {
            ObjectListIterator var13 = this.toBlow.iterator();

            while(var13.hasNext()) {
                BlockPos blockpos2 = (BlockPos)var13.next();
                if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
                    BlockIgniteEvent event = CraftEventFactory.callBlockIgniteEvent(this.level, blockpos2.getX(), blockpos2.getY(), blockpos2.getZ(), (Explosion) (Object) this);
                    if (!event.isCancelled()) {
                        this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
                    }
                }
            }
        }
    }

    @Override
    public boolean bridge$wasCanceled() {
        return wasCanceled;
    }

    @Override
    public void taiyitist$setWasCanceled(boolean wasCanceled) {
        this.wasCanceled = wasCanceled;
    }
}
