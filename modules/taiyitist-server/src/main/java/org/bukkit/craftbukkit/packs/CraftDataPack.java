package org.bukkit.craftbukkit.packs;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.InclusiveRange;
import org.bukkit.Bukkit;
import org.bukkit.FeatureFlag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftFeatureFlag;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.packs.DataPack;

public class CraftDataPack implements DataPack {
   private final Pack handle;
   private final PackMetadataSection resourcePackInfo;

   public CraftDataPack(Pack handler) {
      this.handle = handler;

      try {
         PackResources iresourcepack = this.handle.resources.openPrimary(this.handle.location());

         try {
            this.resourcePackInfo = (PackMetadataSection)iresourcepack.getMetadataSection(PackMetadataSection.TYPE);
         } catch (Throwable var6) {
            if (iresourcepack != null) {
               try {
                  iresourcepack.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (iresourcepack != null) {
            iresourcepack.close();
         }

      } catch (IOException var7) {
         IOException e = var7;
         throw new RuntimeException(e);
      }
   }

   public Pack getHandle() {
      return this.handle;
   }

   public String getRawId() {
      return this.getHandle().getId();
   }

   public String getTitle() {
      return CraftChatMessage.fromComponent(this.getHandle().getTitle());
   }

   public String getDescription() {
      return CraftChatMessage.fromComponent(this.getHandle().getDescription());
   }

   public int getPackFormat() {
      return this.resourcePackInfo.packFormat();
   }

   public int getMinSupportedPackFormat() {
      return (Integer)((InclusiveRange)this.resourcePackInfo.supportedFormats().orElse(new InclusiveRange(this.getPackFormat()))).minInclusive();
   }

   public int getMaxSupportedPackFormat() {
      return (Integer)((InclusiveRange)this.resourcePackInfo.supportedFormats().orElse(new InclusiveRange(this.getPackFormat()))).maxInclusive();
   }

   public boolean isRequired() {
      return this.getHandle().isRequired();
   }

   public DataPack.Compatibility getCompatibility() {
      DataPack.Compatibility var10000;
      switch (this.getHandle().getCompatibility()) {
         case COMPATIBLE -> var10000 = Compatibility.COMPATIBLE;
         case TOO_NEW -> var10000 = Compatibility.NEW;
         case TOO_OLD -> var10000 = Compatibility.OLD;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isEnabled() {
      return ((CraftServer)Bukkit.getServer()).getServer().getPackRepository().getSelectedIds().contains(this.getRawId());
   }

   public DataPack.Source getSource() {
      if (this.getHandle().getPackSource() == PackSource.BUILT_IN) {
         return Source.BUILT_IN;
      } else if (this.getHandle().getPackSource() == PackSource.FEATURE) {
         return Source.FEATURE;
      } else if (this.getHandle().getPackSource() == PackSource.WORLD) {
         return Source.WORLD;
      } else {
         return this.getHandle().getPackSource() == PackSource.SERVER ? Source.SERVER : Source.DEFAULT;
      }
   }

   public Set<FeatureFlag> getRequestedFeatures() {
      Stream var10000 = CraftFeatureFlag.getFromNMS(this.getHandle().getRequestedFeatures()).stream();
      Objects.requireNonNull(FeatureFlag.class);
      return (Set)var10000.map(FeatureFlag.class::cast).collect(Collectors.toUnmodifiableSet());
   }

   public NamespacedKey getKey() {
      return NamespacedKey.fromString(this.getRawId());
   }

   public String toString() {
      String requestedFeatures = (String)this.getRequestedFeatures().stream().map((featureFlag) -> {
         return featureFlag.getKey().toString();
      }).collect(Collectors.joining(","));
      String var10000 = this.getRawId();
      return "CraftDataPack{rawId=" + var10000 + ",id=" + String.valueOf(this.getKey()) + ",title=" + this.getTitle() + ",description=" + this.getDescription() + ",packformat=" + this.getPackFormat() + ",minSupportedPackFormat=" + this.getMinSupportedPackFormat() + ",maxSupportedPackFormat=" + this.getMaxSupportedPackFormat() + ",compatibility=" + String.valueOf(this.getCompatibility()) + ",source=" + String.valueOf(this.getSource()) + ",enabled=" + this.isEnabled() + ",requestedFeatures=[" + requestedFeatures + "]}";
   }
}
