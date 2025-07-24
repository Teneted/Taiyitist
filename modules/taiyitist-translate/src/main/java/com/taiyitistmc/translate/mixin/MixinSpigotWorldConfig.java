package com.taiyitistmc.translate.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.taiyitistmc.TaiyitistMCStart;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@Mixin(value = SpigotWorldConfig.class, remap = false)
public abstract class MixinSpigotWorldConfig {

    @Shadow protected abstract void log(String s);

    @Shadow @Final private String worldName;

    @Shadow public int viewDistance;

    @Shadow public int itemDespawnRate;

    @Shadow public int animalActivationRange;

    @Shadow public int monsterActivationRange;

    @Shadow public int raiderActivationRange;

    @Shadow public int miscActivationRange;

    @Shadow public boolean tickInactiveVillagers;

    @Shadow public boolean ignoreSpectatorActivation;

    @Shadow public byte mobSpawnRange;

    @Shadow public double expMerge;

    @Shadow public double itemMerge;

    @Shadow public int playerTrackingRange;

    @Shadow public int animalTrackingRange;

    @Shadow public int monsterTrackingRange;

    @Shadow public int miscTrackingRange;

    @Shadow public int displayTrackingRange;

    @Shadow public int otherTrackingRange;

    @Shadow protected abstract int getInt(String path, int def);

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$init(SpigotWorldConfig instance, String s) {
        this.log( TaiyitistMCStart.I18N.as("spigotworldconfig.1") + this.worldName + "] --------");
    }

    @Redirect(method = "activationRange", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$activationRange(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.2") + this.animalActivationRange + " / Mo " + this.monsterActivationRange + " / Ra " + this.raiderActivationRange + " / Mi " + this.miscActivationRange + " / Tiv " + this.tickInactiveVillagers + " / Isa " + this.ignoreSpectatorActivation);
    }

    @Redirect(method = "itemDespawnRate", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$itemDespawnRate(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.3") + this.itemDespawnRate);
    }

    @Redirect(method = "mobSpawnRange", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$mobSpawnRange(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.4") + this.mobSpawnRange);
    }

    @Redirect(method = "viewDistance", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$viewDistance(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.5") + this.viewDistance);
    }

    @Redirect(method = "expMerge", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$expMerge(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.6") + this.expMerge);
    }

    @Redirect(method = "itemMerge", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$itemMerge(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.7") + this.itemMerge);
    }

    @Redirect(method = "getAndValidateGrowth", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V", ordinal = 1))
    private void bosom$cactus(SpigotWorldConfig instance, String s, @Local(argsOnly = true) String crop, @Local int modifier) {
        this.log(crop + TaiyitistMCStart.I18N.as("spigotworldconfig.8") + modifier + "%");
    }

    @Redirect(method = "trackingRange", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;log(Ljava/lang/String;)V"))
    private void bosom$trackingRange(SpigotWorldConfig instance, String s) {
        this.log(TaiyitistMCStart.I18N.as("spigotworldconfig.9") +" Pl " + this.playerTrackingRange + " / An " + this.animalTrackingRange + " / Mo " + this.monsterTrackingRange + " / Mi " + this.miscTrackingRange + " / Di " + this.displayTrackingRange + " / Other " + this.otherTrackingRange);
    }

    @Redirect(method = "getAndValidateGrowth", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getInt(Ljava/lang/String;I)I"))
    private int bosom$resetModifier(SpigotWorldConfig instance, String path, int def, @Local(argsOnly = true) String crop) {
        return this.getInt("growth." + crop.toLowerCase(Locale.ENGLISH) + "-", 100);
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 0), index = 0)
    private String bosom$growthModifiers0(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.cactus");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 1), index = 0)
    private String bosom$growthModifiers1(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.cane");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 2), index = 0)
    private String bosom$growthModifiers2(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.melon");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 3), index = 0)
    private String bosom$growthModifiers3(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.mushroom");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 4), index = 0)
    private String bosom$growthModifiers4(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.pumpkin");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 5), index = 0)
    private String bosom$growthModifiers5(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.sapling");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 6), index = 0)
    private String bosom$growthModifiers6(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.beetroot");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 7), index = 0)
    private String bosom$growthModifiers7(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.carrot");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 8), index = 0)
    private String bosom$growthModifiers8(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.potato");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 9), index = 0)
    private String bosom$growthModifiers9(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.wheat");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 10), index = 0)
    private String bosom$growthModifiers10(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.netherWart");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 11), index = 0)
    private String bosom$growthModifiers11(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.vine");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 12), index = 0)
    private String bosom$growthModifiers12(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.cocoa");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 13), index = 0)
    private String bosom$growthModifiers13(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.bamboo");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 14), index = 0)
    private String bosom$growthModifiers14(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.sweetBerry");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 15), index = 0)
    private String bosom$growthModifiers15(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.kelp");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 16), index = 0)
    private String bosom$growthModifiers16(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.twistingVines");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 17), index = 0)
    private String bosom$growthModifiers17(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.weepingVines");
    }

    @ModifyArg(method = "growthModifiers", at = @At(value = "INVOKE", target = "Lorg/spigotmc/SpigotWorldConfig;getAndValidateGrowth(Ljava/lang/String;)I", ordinal = 18), index = 0)
    private String bosom$growthModifiers18(String crop) {
        return TaiyitistMCStart.I18N.as("spigotworldconfig.growthModifiers.caveVines");
    }
}
