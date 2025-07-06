package org.bukkit.craftbukkit.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.taiyitistmc.bukkit.BukkitMethodHooks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class CraftMapView implements MapView {
   private final Map<CraftPlayer, RenderData> renderCache = new HashMap();
   private final List<MapRenderer> renderers = new ArrayList();
   private final Map<MapRenderer, Map<CraftPlayer, CraftMapCanvas>> canvases = new HashMap();
   protected final MapItemSavedData worldMap;

   public CraftMapView(MapItemSavedData worldMap) {
      this.worldMap = worldMap;
      this.addRenderer(new CraftMapRenderer(this, worldMap));
   }

   public int getId() {
      return this.worldMap.bridge$mapView().getId();
   }

   public boolean isVirtual() {
      return this.renderers.size() > 0 && !(this.renderers.get(0) instanceof CraftMapRenderer);
   }

   public MapView.Scale getScale() {
      return Scale.valueOf(this.worldMap.scale);
   }

   public void setScale(MapView.Scale scale) {
      this.worldMap.scale = scale.getValue();
   }

   public World getWorld() {
      ResourceKey<Level> dimension = this.worldMap.dimension;
      ServerLevel world = BukkitMethodHooks.getServer().getLevel(dimension);
      if (world != null) {
         return world.getWorld();
      } else {
         return this.worldMap.bridge$uniqueId() != null ? Bukkit.getServer().getWorld(this.worldMap.bridge$uniqueId()) : null;
      }
   }

   public void setWorld(World world) {
      this.worldMap.dimension = ((CraftWorld)world).getHandle().dimension();
      this.worldMap.banner$setUniqueId(world.getUID());
   }

   public int getCenterX() {
      return this.worldMap.centerX;
   }

   public int getCenterZ() {
      return this.worldMap.centerZ;
   }

   public void setCenterX(int x) {
      this.worldMap.centerX = x;
   }

   public void setCenterZ(int z) {
      this.worldMap.centerZ = z;
   }

   public List<MapRenderer> getRenderers() {
      return new ArrayList(this.renderers);
   }

   public void addRenderer(MapRenderer renderer) {
      if (!this.renderers.contains(renderer)) {
         this.renderers.add(renderer);
         this.canvases.put(renderer, new HashMap());
         renderer.initialize(this);
      }

   }

   public boolean removeRenderer(MapRenderer renderer) {
      if (!this.renderers.contains(renderer)) {
         return false;
      } else {
         this.renderers.remove(renderer);
         Iterator var2 = ((Map)this.canvases.get(renderer)).entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<CraftPlayer, CraftMapCanvas> entry = (Map.Entry)var2.next();

            for(int x = 0; x < 128; ++x) {
               for(int y = 0; y < 128; ++y) {
                  ((CraftMapCanvas)entry.getValue()).setPixel(x, y, (byte)-1);
               }
            }
         }

         this.canvases.remove(renderer);
         return true;
      }
   }

   private boolean isContextual() {
      Iterator var1 = this.renderers.iterator();

      MapRenderer renderer;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         renderer = (MapRenderer)var1.next();
      } while(!renderer.isContextual());

      return true;
   }

   public RenderData render(CraftPlayer player) {
      boolean context = this.isContextual();
      RenderData render = (RenderData)this.renderCache.get(context ? player : null);
      if (render == null) {
         render = new RenderData();
         this.renderCache.put(context ? player : null, render);
      }

      if (context && this.renderCache.containsKey((Object)null)) {
         this.renderCache.remove((Object)null);
      }

      Arrays.fill(render.buffer, (byte)0);
      render.cursors.clear();
      Iterator var4 = this.renderers.iterator();

      while(var4.hasNext()) {
         MapRenderer renderer = (MapRenderer)var4.next();
         CraftMapCanvas canvas = (CraftMapCanvas)((Map)this.canvases.get(renderer)).get(renderer.isContextual() ? player : null);
         if (canvas == null) {
            canvas = new CraftMapCanvas(this);
            ((Map)this.canvases.get(renderer)).put(renderer.isContextual() ? player : null, canvas);
         }

         canvas.setBase(render.buffer);

         try {
            renderer.render(this, canvas, player);
         } catch (Throwable var10) {
            Throwable ex = var10;
            Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Could not render map using renderer " + renderer.getClass().getName(), ex);
         }

         byte[] buf = canvas.getBuffer();

         int i;
         for(i = 0; i < buf.length; ++i) {
            byte color = buf[i];
            if (color >= 0 || color <= -9) {
               render.buffer[i] = color;
            }
         }

         for(i = 0; i < canvas.getCursors().size(); ++i) {
            render.cursors.add(canvas.getCursors().getCursor(i));
         }
      }

      return render;
   }

   public boolean isTrackingPosition() {
      return this.worldMap.trackingPosition;
   }

   public void setTrackingPosition(boolean trackingPosition) {
      this.worldMap.trackingPosition = trackingPosition;
   }

   public boolean isUnlimitedTracking() {
      return this.worldMap.unlimitedTracking;
   }

   public void setUnlimitedTracking(boolean unlimited) {
      this.worldMap.unlimitedTracking = unlimited;
   }

   public boolean isLocked() {
      return this.worldMap.locked;
   }

   public void setLocked(boolean locked) {
      this.worldMap.locked = locked;
   }
}
