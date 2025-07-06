package org.bukkit.craftbukkit.v1_21_R5.entity;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.entity.EntityFactory;
import org.bukkit.entity.EntitySnapshot;

public class CraftEntityFactory implements EntityFactory {
   private final RegistryAccess registry;

   public CraftEntityFactory(RegistryAccess registry) {
      this.registry = registry;
   }

   public EntitySnapshot createEntitySnapshot(String input) {
      Preconditions.checkArgument(input != null, "Input string cannot be null");

      CompoundTag tag;
      try {
         tag = TagParser.parseCompoundFully(input);
      } catch (CommandSyntaxException var5) {
         CommandSyntaxException e = var5;
         throw new IllegalArgumentException("Could not parse Entity: " + input, e);
      }

      ValueInput value = TagValueInput.create(ProblemReporter.DISCARDING, this.registry, tag);
      EntityType<?> type = (EntityType)EntityType.by(value).orElse((Object)null);
      if (type == null) {
         throw new IllegalArgumentException("Could not parse Entity: " + input);
      } else {
         return CraftEntitySnapshot.create(tag, CraftEntityType.minecraftToBukkit(type));
      }
   }
}
