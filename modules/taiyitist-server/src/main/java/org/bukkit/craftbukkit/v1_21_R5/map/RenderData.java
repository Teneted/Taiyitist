package org.bukkit.craftbukkit.v1_21_R5.map;

import java.util.ArrayList;
import org.bukkit.map.MapCursor;

public class RenderData {
   public final byte[] buffer = new byte[16384];
   public final ArrayList<MapCursor> cursors = new ArrayList();
}
