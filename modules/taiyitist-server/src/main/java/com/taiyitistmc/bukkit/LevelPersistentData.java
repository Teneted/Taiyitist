package com.taiyitistmc.bukkit;

import com.mojang.datafixers.DSL;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.taiyitistmc.util.EnumHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.bukkit.craftbukkit.CraftWorld;

public class LevelPersistentData extends SavedData {

    private CompoundTag tag;
    private static final DSL.TypeReference PDC_TYPE = () -> "bukkit_pdc";
    public static final DataFixTypes BUKKIT_PDC =
            EnumHelper.addEnum(DataFixTypes.class, "BUKKIT_PDC", List.of(DSL.TypeReference.class), List.of(PDC_TYPE));
    public static final Codec<LevelPersistentData> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(CompoundTag.CODEC.fieldOf("tag")
                    .forGetter(LevelPersistentData::getTag))
                    .apply(instance, LevelPersistentData::new));

    public LevelPersistentData(CompoundTag tag) {
        this.tag = tag == null ? new CompoundTag() : tag;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public void save(CraftWorld world) {
        this.tag = new CompoundTag();
        world.storeBukkitValues(this.tag);
    }

    public static SavedDataType<LevelPersistentData> factory() {
        return new SavedDataType<>("bukkit_pdc", () -> new LevelPersistentData(new CompoundTag()), CODEC, BUKKIT_PDC);
    }
}
