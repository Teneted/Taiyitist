package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.item.component.WritableBookContent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.WritableBookMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBook extends CraftMetaItem implements BookMeta, WritableBookMeta {
   static final CraftMetaItem.ItemMetaKeyType<WritableBookContent> BOOK_CONTENT;
   static final ItemMetaKey BOOK_PAGES;
   static final int MAX_PAGES = Integer.MAX_VALUE;
   static final int MAX_PAGE_LENGTH = 1024;
   protected List<String> pages;
   private BookMeta.Spigot spigot = new SpigotMeta();

   CraftMetaBook(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaBook bookMeta) {
         if (bookMeta.pages != null) {
            this.pages = new ArrayList(bookMeta.pages.size());
            this.pages.addAll(bookMeta.pages);
         }
      } else if (meta instanceof CraftMetaBookSigned bookMeta) {
         if (bookMeta.pages != null) {
            this.pages = new ArrayList(bookMeta.pages.size());
            this.pages.addAll(Lists.transform(bookMeta.pages, CraftChatMessage::fromComponent));
         }
      }

   }

   CraftMetaBook(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, BOOK_CONTENT).ifPresent((writable) -> {
         List<Filterable<String>> pages = writable.pages();
         this.pages = new ArrayList(pages.size());

         for(int i = 0; i < Math.min(pages.size(), Integer.MAX_VALUE); ++i) {
            String page = (String)((Filterable)pages.get(i)).raw();
            page = this.validatePage(page);
            this.pages.add(page);
         }

      });
   }

   CraftMetaBook(Map<String, Object> map) {
      super(map);
      Iterable<?> pages = (Iterable)SerializableMeta.getObject(Iterable.class, map, BOOK_PAGES.BUKKIT, true);
      if (pages != null) {
         this.pages = new ArrayList();
         Iterator var3 = pages.iterator();

         while(var3.hasNext()) {
            Object page = var3.next();
            if (page instanceof String) {
               this.internalAddPage(this.validatePage((String)page));
            }
         }
      }

   }

   void applyToItem(CraftMetaItem.Applicator itemData) {
      super.applyToItem(itemData);
      if (this.pages != null) {
         List<Filterable<String>> list = new ArrayList();
         Iterator var3 = this.pages.iterator();

         while(var3.hasNext()) {
            String page = (String)var3.next();
            list.add(Filterable.from(FilteredText.passThrough(page)));
         }

         itemData.put(BOOK_CONTENT, new WritableBookContent(list));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBookEmpty();
   }

   boolean isBookEmpty() {
      return this.pages == null;
   }

   boolean applicableTo(Material type) {
      return type == Material.WRITABLE_BOOK;
   }

   public boolean hasAuthor() {
      return false;
   }

   public boolean hasTitle() {
      return false;
   }

   public boolean hasPages() {
      return this.pages != null && !this.pages.isEmpty();
   }

   public boolean hasGeneration() {
      return false;
   }

   public String getTitle() {
      return null;
   }

   public boolean setTitle(String title) {
      return false;
   }

   public String getAuthor() {
      return null;
   }

   public void setAuthor(String author) {
   }

   public BookMeta.Generation getGeneration() {
      return null;
   }

   public void setGeneration(BookMeta.Generation generation) {
   }

   public String getPage(int page) {
      Preconditions.checkArgument(this.isValidPage(page), "Invalid page number (%s)", page);
      return (String)this.pages.get(page - 1);
   }

   public void setPage(int page, String text) {
      Preconditions.checkArgument(this.isValidPage(page), "Invalid page number (%s/%s)", page, this.getPageCount());
      String newText = this.validatePage(text);
      this.pages.set(page - 1, newText);
   }

   public void setPages(String... pages) {
      this.setPages(Arrays.asList(pages));
   }

   public void addPage(String... pages) {
      String[] var2 = pages;
      int var3 = pages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String page = var2[var4];
         page = this.validatePage(page);
         this.internalAddPage(page);
      }

   }

   String validatePage(String page) {
      if (page == null) {
         page = "";
      } else if (page.length() > 1024) {
         page = page.substring(0, 1024);
      }

      return page;
   }

   private void internalAddPage(String page) {
      if (this.pages == null) {
         this.pages = new ArrayList();
      } else if (this.pages.size() >= Integer.MAX_VALUE) {
         return;
      }

      this.pages.add(page);
   }

   public int getPageCount() {
      return this.pages == null ? 0 : this.pages.size();
   }

   public List<String> getPages() {
      return (List)(this.pages == null ? ImmutableList.of() : (List)this.pages.stream().collect(ImmutableList.toImmutableList()));
   }

   public void setPages(List<String> pages) {
      if (pages.isEmpty()) {
         this.pages = null;
      } else {
         if (this.pages != null) {
            this.pages.clear();
         }

         Iterator var2 = pages.iterator();

         while(var2.hasNext()) {
            String page = (String)var2.next();
            this.addPage(page);
         }

      }
   }

   private boolean isValidPage(int page) {
      return page > 0 && page <= this.getPageCount();
   }

   public CraftMetaBook clone() {
      CraftMetaBook meta = (CraftMetaBook)super.clone();
      if (this.pages != null) {
         meta.pages = new ArrayList(this.pages);
      }

      Objects.requireNonNull(meta);
      meta.spigot = meta.new SpigotMeta();
      return meta;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.pages != null) {
         hash = 61 * hash + 17 * this.pages.hashCode();
      }

      return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (meta instanceof CraftMetaBook) {
         CraftMetaBook that = (CraftMetaBook)meta;
         return Objects.equals(this.pages, that.pages);
      } else {
         return true;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBook || this.isBookEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.pages != null) {
         builder.put(BOOK_PAGES.BUKKIT, ImmutableList.copyOf(this.pages));
      }

      return builder;
   }

   public BookMeta.Spigot spigot() {
      return this.spigot;
   }

   static {
      BOOK_CONTENT = new CraftMetaItem.ItemMetaKeyType(DataComponents.WRITABLE_BOOK_CONTENT);
      BOOK_PAGES = new ItemMetaKey("pages");
   }

   private class SpigotMeta extends BookMeta.Spigot {
      private String pageToJSON(String page) {
         Component component = CraftChatMessage.fromString(page, true, true)[0];
         return CraftChatMessage.toJSON(component);
      }

      private String componentsToPage(BaseComponent[] components) {
         Component component = CraftChatMessage.fromJSONOrNull(CraftChatMessage.getBungee().toString(components));
         return CraftChatMessage.fromComponent(component);
      }

      public BaseComponent[] getPage(int page) {
         Preconditions.checkArgument(CraftMetaBook.this.isValidPage(page), "Invalid page number");
         return CraftChatMessage.getBungee().parse(this.pageToJSON((String)CraftMetaBook.this.pages.get(page - 1)));
      }

      public void setPage(int page, BaseComponent... text) {
         if (!CraftMetaBook.this.isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + CraftMetaBook.this.getPageCount());
         } else {
            BaseComponent[] newText = text == null ? new BaseComponent[0] : text;
            CraftMetaBook.this.pages.set(page - 1, this.componentsToPage(newText));
         }
      }

      public void setPages(BaseComponent[]... pages) {
         this.setPages(Arrays.asList(pages));
      }

      public void addPage(BaseComponent[]... pages) {
         BaseComponent[][] var2 = pages;
         int var3 = pages.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            BaseComponent[] page = var2[var4];
            if (page == null) {
               page = new BaseComponent[0];
            }

            CraftMetaBook.this.internalAddPage(this.componentsToPage(page));
         }

      }

      public List<BaseComponent[]> getPages() {
         if (CraftMetaBook.this.pages == null) {
            return ImmutableList.of();
         } else {
            final List<String> copy = ImmutableList.copyOf(CraftMetaBook.this.pages);
            return new AbstractList<BaseComponent[]>() {
               public BaseComponent[] get(int index) {
                  return CraftChatMessage.getBungee().parse(SpigotMeta.this.pageToJSON((String)copy.get(index)));
               }

               public int size() {
                  return copy.size();
               }
            };
         }
      }

      public void setPages(List<BaseComponent[]> pages) {
         if (pages.isEmpty()) {
            CraftMetaBook.this.pages = null;
         } else {
            if (CraftMetaBook.this.pages != null) {
               CraftMetaBook.this.pages.clear();
            }

            Iterator var2 = pages.iterator();

            while(var2.hasNext()) {
               BaseComponent[] page = (BaseComponent[])var2.next();
               this.addPage(page);
            }

         }
      }
   }
}
