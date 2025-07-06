package org.bukkit.craftbukkit.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WorldUUID {
   private static final Logger LOGGER = LogManager.getLogger();

   private WorldUUID() {
   }

   public static UUID getUUID(File baseDir) {
      File file1 = new File(baseDir, "uid.dat");
      if (file1.exists()) {
         label192: {
            DataInputStream dis = null;

            UUID var34;
            try {
               dis = new DataInputStream(new FileInputStream(file1));
               var34 = new UUID(dis.readLong(), dis.readLong());
            } catch (IOException var31) {
               IOException ex = var31;
               LOGGER.warn("Failed to read " + String.valueOf(file1) + ", generating new random UUID", ex);
               break label192;
            } finally {
               if (dis != null) {
                  try {
                     dis.close();
                  } catch (IOException var28) {
                  }
               }

            }

            return var34;
         }
      }

      UUID uuid = UUID.randomUUID();
      DataOutputStream dos = null;

      try {
         dos = new DataOutputStream(new FileOutputStream(file1));
         dos.writeLong(uuid.getMostSignificantBits());
         dos.writeLong(uuid.getLeastSignificantBits());
      } catch (IOException var29) {
         IOException ex = var29;
         LOGGER.warn("Failed to write " + String.valueOf(file1), ex);
      } finally {
         if (dos != null) {
            try {
               dos.close();
            } catch (IOException var27) {
            }
         }

      }

      return uuid;
   }
}
