package org.bukkit.craftbukkit.v1_21_R5.util;

import java.io.File;
import java.io.FilenameFilter;

public class DatFileFilter implements FilenameFilter {
   public boolean accept(File dir, String name) {
      return name.endsWith(".dat");
   }
}
