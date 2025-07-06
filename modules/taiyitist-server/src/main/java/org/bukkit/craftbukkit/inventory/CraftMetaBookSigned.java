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
import net.minecraft.world.item.component.WrittenBookContent;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.meta.BookMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaBookSigned extends CraftMetaItem implements BookMeta {
   static final CraftMetaItem.ItemMetaKeyType<WrittenBookContent> BOOK_CONTENT;
   static final ItemMetaKey BOOK_TITLE;
   static final ItemMetaKey BOOK_AUTHOR;
   static final ItemMetaKey BOOK_PAGES;
   static final ItemMetaKey RESOLVED;
   static final ItemMetaKey GENERATION;
   static final int MAX_PAGES = Integer.MAX_VALUE;
   static final int MAX_PAGE_LENGTH = 1024;
   static final int MAX_TITLE_LENGTH = 32;
   protected String title;
   protected String author;
   protected List<Component> pages;
   protected boolean resolved;
   protected int generation;
   private BookMeta.Spigot spigot = new SpigotMeta();

   CraftMetaBookSigned(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaBookSigned bookMeta) {
         this.title = bookMeta.title;
         this.author = bookMeta.author;
         this.resolved = bookMeta.resolved;
         this.generation = bookMeta.generation;
         if (bookMeta.pages != null) {
            this.pages = new ArrayList(bookMeta.pages.size());
            this.pages.addAll(bookMeta.pages);
         }
      } else if (meta instanceof CraftMetaBook bookMeta) {
         if (bookMeta.pages != null) {
            this.pages = new ArrayList(bookMeta.pages.size());
            Iterator var3 = bookMeta.pages.iterator();

            while(var3.hasNext()) {
               String page = (String)var3.next();
               Component component = CraftChatMessage.fromString(page, true, true)[0];
               this.pages.add(component);
            }
         }
      }

   }

   CraftMetaBookSigned(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, BOOK_CONTENT).ifPresent((written) -> {
         this.title = (String)written.title().raw();
         this.author = written.author();
         this.resolved = written.resolved();
         this.generation = written.generation();
         List<Filterable<Component>> pages = written.pages();
         this.pages = new ArrayList(pages.size());

         for(int i = 0; i < Math.min(pages.size(), Integer.MAX_VALUE); ++i) {
            Component page = (Component)((Filterable)pages.get(i)).raw();
            this.pages.add(page);
         }

      });
   }

   CraftMetaBookSigned(Map<String, Object> map) {
      super(map);
      this.setAuthor(SerializableMeta.getString(map, BOOK_AUTHOR.BUKKIT, true));
      this.setTitle(SerializableMeta.getString(map, BOOK_TITLE.BUKKIT, true));
      Iterable<?> pages = (Iterable)SerializableMeta.getObject(Iterable.class, map, BOOK_PAGES.BUKKIT, true);
      if (pages != null) {
         this.pages = new ArrayList();
         Iterator var3 = pages.iterator();

         while(var3.hasNext()) {
            Object page = var3.next();
            if (page instanceof String) {
               this.internalAddPage(CraftChatMessage.fromJSONOrString((String)page, false, true, 1024, false));
            }
         }
      }

      this.resolved = SerializableMeta.getBoolean(map, RESOLVED.BUKKIT);
      this.generation = SerializableMeta.getInteger(map, GENERATION.BUKKIT);
   }

   void applyToItem(CraftMetaItem.Applicator itemData) {
      super.applyToItem(itemData);
      if (this.pages != null) {
         List<Filterable<Component>> list = new ArrayList();
         Iterator var3 = this.pages.iterator();

         while(var3.hasNext()) {
            Component page = (Component)var3.next();
            list.add(Filterable.passThrough(page));
         }

         itemData.put(BOOK_CONTENT, new WrittenBookContent(Filterable.from(FilteredText.passThrough(this.title)), this.author, this.generation, list, this.resolved));
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBookEmpty();
   }

   boolean isBookEmpty() {
      return this.pages == null && !this.hasAuthor() && !this.hasTitle() && !this.hasGeneration() && !this.resolved;
   }

   boolean applicableTo(Material type) {
      return type == Material.WRITTEN_BOOK;
   }

   public boolean hasAuthor() {
      return this.author != null;
   }

   public boolean hasTitle() {
      return this.title != null;
   }

   public boolean hasPages() {
      return this.pages != null && !this.pages.isEmpty();
   }

   public boolean hasGeneration() {
      return this.generation != 0;
   }

   public String getTitle() {
      return this.title;
   }

   public boolean setTitle(String title) {
      if (title == null) {
         this.title = null;
         return true;
      } else if (title.length() > 32) {
         return false;
      } else {
         this.title = title;
         return true;
      }
   }

   public String getAuthor() {
      return this.author;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public BookMeta.Generation getGeneration() {
      return Generation.values()[this.generation];
   }

   public void setGeneration(BookMeta.Generation generation) {
      this.generation = generation == null ? 0 : generation.ordinal();
   }

   public String getPage(int page) {
      Preconditions.checkArgument(this.isValidPage(page), "Invalid page number (%s)", page);
      return CraftChatMessage.fromComponent((Component)this.pages.get(page - 1));
   }

   public void setPage(int page, String text) {
      Preconditions.checkArgument(this.isValidPage(page), "Invalid page number (%s/%s)", page, this.getPageCount());
      String newText = this.validatePage(text);
      this.pages.set(page - 1, CraftChatMessage.fromStringOrEmpty(newText, true));
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
         this.internalAddPage(CraftChatMessage.fromStringOrEmpty(page, true));
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

   private void internalAddPage(Component page) {
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
      return (List)(this.pages == null ? ImmutableList.of() : (List)this.pages.stream().map(CraftChatMessage::fromComponent).collect(ImmutableList.toImmutableList()));
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

   public boolean isResolved() {
      return this.resolved;
   }

   public void setResolved(boolean resolved) {
      this.resolved = resolved;
   }

   public CraftMetaBookSigned clone() {
      CraftMetaBookSigned meta = (CraftMetaBookSigned)super.clone();
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
      if (this.hasTitle()) {
         hash = 61 * hash + this.title.hashCode();
      }

      if (this.hasAuthor()) {
         hash = 61 * hash + 13 * this.author.hashCode();
      }

      if (this.pages != null) {
         hash = 61 * hash + 17 * this.pages.hashCode();
      }

      if (this.resolved) {
         hash = 61 * hash + 17 * Boolean.hashCode(this.resolved);
      }

      if (this.hasGeneration()) {
         hash = 61 * hash + 19 * Integer.hashCode(this.generation);
      }

      return original != hash ? CraftMetaBook.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaBookSigned)) {
         return true;
      } else {
         boolean var10000;
         label49: {
            CraftMetaBookSigned that = (CraftMetaBookSigned)meta;
            if (this.hasTitle()) {
               if (!that.hasTitle() || !this.title.equals(that.title)) {
                  break label49;
               }
            } else if (that.hasTitle()) {
               break label49;
            }

            if (this.hasAuthor()) {
               if (!that.hasAuthor() || !this.author.equals(that.author)) {
                  break label49;
               }
            } else if (that.hasAuthor()) {
               break label49;
            }

            if (Objects.equals(this.pages, that.pages) && Objects.equals(this.resolved, that.resolved) && Objects.equals(this.generation, that.generation)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaBookSigned || this.isBookEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasTitle()) {
         builder.put(BOOK_TITLE.BUKKIT, this.title);
      }

      if (this.hasAuthor()) {
         builder.put(BOOK_AUTHOR.BUKKIT, this.author);
      }

      if (this.pages != null) {
         builder.put(BOOK_PAGES.BUKKIT, ImmutableList.copyOf(Lists.transform(this.pages, CraftChatMessage::toJSON)));
      }

      if (this.resolved) {
         builder.put(RESOLVED.BUKKIT, this.resolved);
      }

      if (this.generation != 0) {
         builder.put(GENERATION.BUKKIT, this.generation);
      }

      return builder;
   }

   public BookMeta.Spigot spigot() {
      return this.spigot;
   }

   static {
      BOOK_CONTENT = new CraftMetaItem.ItemMetaKeyType(DataComponents.WRITTEN_BOOK_CONTENT);
      BOOK_TITLE = new ItemMetaKey("title");
      BOOK_AUTHOR = new ItemMetaKey("author");
      BOOK_PAGES = new ItemMetaKey("pages");
      RESOLVED = new ItemMetaKey("resolved");
      GENERATION = new ItemMetaKey("generation");
   }

   private class SpigotMeta extends BookMeta.Spigot {
      private String pageToJSON(Component page) {
         return CraftChatMessage.toJSON(page);
      }

      private Component componentsToPage(BaseComponent[] components) {
         return CraftChatMessage.fromJSON(CraftChatMessage.getBungee().toString(components));
      }

      public BaseComponent[] getPage(int page) {
         Preconditions.checkArgument(CraftMetaBookSigned.this.isValidPage(page), "Invalid page number");
         return CraftChatMessage.getBungee().parse(this.pageToJSON((Component)CraftMetaBookSigned.this.pages.get(page - 1)));
      }

      public void setPage(int page, BaseComponent... text) {
         if (!CraftMetaBookSigned.this.isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + CraftMetaBookSigned.this.getPageCount());
         } else {
            BaseComponent[] newText = text == null ? new BaseComponent[0] : text;
            CraftMetaBookSigned.this.pages.set(page - 1, this.componentsToPage(newText));
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

            CraftMetaBookSigned.this.internalAddPage(this.componentsToPage(page));
         }

      }

      public List<BaseComponent[]> getPages() {
         if (CraftMetaBookSigned.this.pages == null) {
            return ImmutableList.of();
         } else {
            final List<Component> copy = ImmutableList.copyOf(CraftMetaBookSigned.this.pages);
            return new AbstractList<BaseComponent[]>() {
               public BaseComponent[] get(int index) {
                  return CraftChatMessage.getBungee().parse(SpigotMeta.this.pageToJSON((Component)copy.get(index)));
               }

               public int size() {
                  return copy.size();
               }
            };
         }
      }

      public void setPages(List<BaseComponent[]> pages) {
         if (pages.isEmpty()) {
            CraftMetaBookSigned.this.pages = null;
         } else {
            if (CraftMetaBookSigned.this.pages != null) {
               CraftMetaBookSigned.this.pages.clear();
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
