package org.teneted.taiyitist.mixin.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Mixin(ServerTickRateManager.class)
public abstract class MixinServerTickRateManager extends TickRateManager {

    @Shadow private long scheduledCurrentSprintTicks;

    @Shadow private long remainingSprintTicks;

    @Shadow private long sprintTimeSpend;

    @Shadow @Final private MinecraftServer server;

    @Shadow public abstract void setFrozen(boolean bl);

    @Shadow private boolean previousIsFrozen;

    private AtomicBoolean taiyitist$sendLog = new AtomicBoolean(true);

    @Inject(method = "stopSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickRateManager;finishTickSprint()V"))
    private void taiyitist$pushSendLog(CallbackInfoReturnable<Boolean> cir) {
        taiyitist$sendLog.set(true);
    }

    @Inject(method = "finishTickSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createCommandSourceStack()Lnet/minecraft/commands/CommandSourceStack;"))
    private void taiyitist$pushSendLog0(CallbackInfo ci) {
        taiyitist$sendLog.set(true);
    }

    @WrapWithCondition(method = "finishTickSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;sendSuccess(Ljava/util/function/Supplier;Z)V"))
    private boolean taiyitist$checkSendLog(CommandSourceStack instance, Supplier<Component> supplier, boolean bl) {
        return taiyitist$sendLog.get();
    }

    public boolean stopSprinting(boolean sendLog) {
        taiyitist$sendLog.set(sendLog);
        if (this.remainingSprintTicks > 0L) {
            this.finishTickSprint(sendLog); // CraftBukkit - add sendLog parameter
            return true;
        } else {
            return false;
        }
    }

    private void finishTickSprint(boolean sendLog) {
        taiyitist$sendLog.set(sendLog);
        long l = this.scheduledCurrentSprintTicks - this.remainingSprintTicks;
        double d = Math.max(1.0, (double)this.sprintTimeSpend) / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
        int i = (int)((double)(TimeUtil.MILLISECONDS_PER_SECOND * l) / d);
        String string = String.format("%.2f", l == 0L ? (double)this.millisecondsPerTick() : d / (double)l);
        this.scheduledCurrentSprintTicks = 0L;
        this.sprintTimeSpend = 0L;
        // CraftBukkit start - add sendLog parameter
        if (sendLog) {
            this.server.createCommandSourceStack().sendSuccess(() -> {
                return Component.translatable("commands.tick.sprint.report", i, string);
            }, true);
        }
        // CraftBukkit end
        this.remainingSprintTicks = 0L;
        this.setFrozen(this.previousIsFrozen);
        this.server.onTickRateChanged();
    }
}
