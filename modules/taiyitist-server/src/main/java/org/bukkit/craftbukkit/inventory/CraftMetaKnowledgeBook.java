package org.bukkit.craftbukkit.inventory;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

@DelegateDeserialization(SerializableMeta.class)
public class CraftMetaKnowledgeBook extends CraftMetaItem implements KnowledgeBookMeta {
   static final CraftMetaItem.ItemMetaKeyType<List<ResourceKey<Recipe<?>>>> BOOK_RECIPES;
   static final int MAX_RECIPES = 32767;
   protected List<NamespacedKey> recipes = new ArrayList();

   CraftMetaKnowledgeBook(CraftMetaItem meta) {
      super(meta);
      if (meta instanceof CraftMetaKnowledgeBook bookMeta) {
         this.recipes.addAll(bookMeta.recipes);
      }

   }

   CraftMetaKnowledgeBook(DataComponentPatch tag) {
      super(tag);
      getOrEmpty(tag, BOOK_RECIPES).ifPresent((pages) -> {
         for(int i = 0; i < pages.size(); ++i) {
            ResourceLocation recipe = ((ResourceKey)pages.get(i)).location();
            this.addRecipe(CraftNamespacedKey.fromMinecraft(recipe));
         }

      });
   }

   CraftMetaKnowledgeBook(Map<String, Object> map) {
      super(map);
      Iterable<?> pages = (Iterable)SerializableMeta.getObject(Iterable.class, map, BOOK_RECIPES.BUKKIT, true);
      if (pages != null) {
         Iterator var3 = pages.iterator();

         while(var3.hasNext()) {
            Object page = var3.next();
            if (page instanceof String) {
               this.addRecipe(CraftNamespacedKey.fromString((String)page));
            }
         }
      }

   }

   void applyToItem(CraftMetaItem.Applicator itemData) {
      super.applyToItem(itemData);
      if (this.hasRecipes()) {
         List<ResourceKey<Recipe<?>>> list = new ArrayList();
         Iterator var3 = this.recipes.iterator();

         while(var3.hasNext()) {
            NamespacedKey recipe = (NamespacedKey)var3.next();
            list.add(CraftRecipe.toMinecraft(recipe));
         }

         itemData.put(BOOK_RECIPES, list);
      }

   }

   boolean isEmpty() {
      return super.isEmpty() && this.isBookEmpty();
   }

   boolean isBookEmpty() {
      return !this.hasRecipes();
   }

   public boolean hasRecipes() {
      return !this.recipes.isEmpty();
   }

   public void addRecipe(NamespacedKey... recipes) {
      NamespacedKey[] var2 = recipes;
      int var3 = recipes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         NamespacedKey recipe = var2[var4];
         if (recipe != null) {
            if (this.recipes.size() >= 32767) {
               return;
            }

            this.recipes.add(recipe);
         }
      }

   }

   public List<NamespacedKey> getRecipes() {
      return Collections.unmodifiableList(this.recipes);
   }

   public void setRecipes(List<NamespacedKey> recipes) {
      this.recipes.clear();
      Iterator var2 = recipes.iterator();

      while(var2.hasNext()) {
         NamespacedKey recipe = (NamespacedKey)var2.next();
         this.addRecipe(recipe);
      }

   }

   public CraftMetaKnowledgeBook clone() {
      CraftMetaKnowledgeBook meta = (CraftMetaKnowledgeBook)super.clone();
      meta.recipes = new ArrayList(this.recipes);
      return meta;
   }

   int applyHash() {
      int original;
      int hash = original = super.applyHash();
      if (this.hasRecipes()) {
         hash = 61 * hash + 17 * this.recipes.hashCode();
      }

      return original != hash ? CraftMetaKnowledgeBook.class.hashCode() ^ hash : hash;
   }

   boolean equalsCommon(CraftMetaItem meta) {
      if (!super.equalsCommon(meta)) {
         return false;
      } else if (!(meta instanceof CraftMetaKnowledgeBook)) {
         return true;
      } else {
         CraftMetaKnowledgeBook that = (CraftMetaKnowledgeBook)meta;
         return this.hasRecipes() ? that.hasRecipes() && this.recipes.equals(that.recipes) : !that.hasRecipes();
      }
   }

   boolean notUncommon(CraftMetaItem meta) {
      return super.notUncommon(meta) && (meta instanceof CraftMetaKnowledgeBook || this.isBookEmpty());
   }

   ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
      super.serialize(builder);
      if (this.hasRecipes()) {
         List<String> recipesString = new ArrayList();
         Iterator var3 = this.recipes.iterator();

         while(var3.hasNext()) {
            NamespacedKey recipe = (NamespacedKey)var3.next();
            recipesString.add(recipe.toString());
         }

         builder.put(BOOK_RECIPES.BUKKIT, recipesString);
      }

      return builder;
   }

   static {
      BOOK_RECIPES = new CraftMetaItem.ItemMetaKeyType(DataComponents.RECIPES, "Recipes");
   }
}
