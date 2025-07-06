package org.bukkit.craftbukkit.v1_21_R5.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.NotNull;

public class CraftNBTTagConfigSerializer {
   private static final Pattern ARRAY = Pattern.compile("^\\[.*]");
   private static final Pattern INTEGER = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)?i", 2);
   private static final Pattern DOUBLE = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final TagParser<Tag> MOJANGSON_PARSER;

   public static String serialize(@NotNull Tag base) {
      SnbtPrinterTagVisitor snbtVisitor = new SnbtPrinterTagVisitor();
      return snbtVisitor.visit(base);
   }

   public static Tag deserialize(Object object) {
      if (object instanceof String snbtString) {
         try {
            return TagParser.parseCompoundFully(snbtString);
         } catch (CommandSyntaxException var3) {
            CommandSyntaxException e = var3;
            throw new RuntimeException("Failed to deserialise nbt", e);
         }
      } else {
         return internalLegacyDeserialization(object);
      }
   }

   private static Tag internalLegacyDeserialization(@NotNull Object object) {
      if (object instanceof Map) {
         CompoundTag compound = new CompoundTag();
         Iterator var11 = ((Map)object).entrySet().iterator();

         while(var11.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var11.next();
            compound.put((String)entry.getKey(), internalLegacyDeserialization(entry.getValue()));
         }

         return compound;
      } else if (!(object instanceof List)) {
         if (object instanceof String) {
            String string = (String)object;
            if (ARRAY.matcher(string).matches()) {
               try {
                  return (Tag)MOJANGSON_PARSER.parseAsArgument(new StringReader(string));
               } catch (CommandSyntaxException var5) {
                  CommandSyntaxException e = var5;
                  throw new RuntimeException("Could not deserialize found list ", e);
               }
            } else if (INTEGER.matcher(string).matches()) {
               return IntTag.valueOf(Integer.parseInt(string.substring(0, string.length() - 1)));
            } else if (DOUBLE.matcher(string).matches()) {
               return DoubleTag.valueOf(Double.parseDouble(string.substring(0, string.length() - 1)));
            } else {
               Tag nbtBase;
               try {
                  nbtBase = (Tag)MOJANGSON_PARSER.parseAsArgument(new StringReader(string));
               } catch (CommandSyntaxException var6) {
                  CommandSyntaxException e = var6;
                  throw new RuntimeException("Could not deserialize found value ", e);
               }

               if (nbtBase instanceof IntTag) {
                  return StringTag.valueOf(String.valueOf(((IntTag)nbtBase).intValue()));
               } else if (nbtBase instanceof DoubleTag) {
                  return StringTag.valueOf(String.valueOf(((DoubleTag)nbtBase).doubleValue()));
               } else {
                  return (Tag)(nbtBase instanceof StringTag ? StringTag.valueOf(string) : nbtBase);
               }
            }
         } else {
            throw new RuntimeException("Could not deserialize NBTBase");
         }
      } else {
         List<Object> list = (List)object;
         if (list.isEmpty()) {
            return new ListTag();
         } else {
            ListTag tagList = new ListTag();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
               Object tag = var3.next();
               tagList.add(internalLegacyDeserialization(tag));
            }

            return tagList;
         }
      }
   }

   static {
      MOJANGSON_PARSER = TagParser.create(NbtOps.INSTANCE);
   }
}
