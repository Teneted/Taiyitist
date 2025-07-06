package org.bukkit.craftbukkit.map;

import java.util.Iterator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CraftMapRenderer extends MapRenderer {
   private final MapItemSavedData worldMap;

   public CraftMapRenderer(CraftMapView mapView, MapItemSavedData worldMap) {
      super(false);
      this.worldMap = worldMap;
   }

   public void render(MapView map, MapCanvas canvas, Player player) {
      for(int x = 0; x < 128; ++x) {
         for(int y = 0; y < 128; ++y) {
            canvas.setPixel(x, y, this.worldMap.colors[y * 128 + x]);
         }
      }

      MapCursorCollection cursors = canvas.getCursors();

      while(cursors.size() > 0) {
         cursors.removeCursor(cursors.getCursor(0));
      }

      Iterator var10 = this.worldMap.decorations.keySet().iterator();

      while(true) {
         String key;
         Player other;
         do {
            if (!var10.hasNext()) {
               return;
            }

            key = (String)var10.next();
            other = Bukkit.getPlayerExact(key);
         } while(other != null && !player.canSee(other));

         MapDecoration decoration = (MapDecoration)this.worldMap.decorations.get(key);
         cursors.addCursor(new MapCursor(decoration.x(), decoration.y(), (byte)(decoration.rot() & 15), CraftMapCursor.CraftType.minecraftHolderToBukkit(decoration.type()), true, CraftChatMessage.fromComponent((Component)decoration.name().orElse((Object)null))));
      }
   }
}
