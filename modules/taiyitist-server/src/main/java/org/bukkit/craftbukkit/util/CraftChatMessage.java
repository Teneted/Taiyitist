package org.bukkit.craftbukkit.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.VersionedComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;

public final class CraftChatMessage {
   private static final Pattern LINK_PATTERN = Pattern.compile("((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf('§') + " \\n]|$))))");
   private static final Map<Character, ChatFormatting> formatMap;

   public static VersionedComponentSerializer getBungee() {
      return VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5);
   }

   public static ChatFormatting getColor(ChatColor color) {
      return (ChatFormatting)formatMap.get(color.getChar());
   }

   public static ChatColor getColor(ChatFormatting format) {
      return ChatColor.getByChar(format.code);
   }

   public static Optional<Component> fromStringOrOptional(String message) {
      return Optional.ofNullable(fromStringOrNull(message));
   }

   public static Optional<Component> fromStringOrOptional(String message, boolean keepNewlines) {
      return Optional.ofNullable(fromStringOrNull(message, keepNewlines));
   }

   public static Component fromStringOrNull(String message) {
      return fromStringOrNull(message, false);
   }

   public static Component fromStringOrNull(String message, boolean keepNewlines) {
      return message != null && !message.isEmpty() ? fromString(message, keepNewlines)[0] : null;
   }

   public static Component fromStringOrEmpty(String message) {
      return fromStringOrEmpty(message, false);
   }

   public static Component fromStringOrEmpty(String message, boolean keepNewlines) {
      return fromString(message, keepNewlines)[0];
   }

   public static Component[] fromString(String message) {
      return fromString(message, false);
   }

   public static Component[] fromString(String message, boolean keepNewlines) {
      return fromString(message, keepNewlines, false);
   }

   public static Component[] fromString(String message, boolean keepNewlines, boolean plain) {
      return (new StringMessage(message, keepNewlines, plain)).getOutput();
   }

   public static String toJSON(Component component) {
      return CraftChatMessage.ChatSerializer.toJson(component, MinecraftServer.getDefaultRegistryAccess());
   }

   public static String toJSONOrNull(Component component) {
      return component == null ? null : toJSON(component);
   }

   public static Component fromJSON(String jsonMessage) throws JsonParseException {
      return CraftChatMessage.ChatSerializer.fromJson(jsonMessage, MinecraftServer.getDefaultRegistryAccess());
   }

   public static Component fromJSONOrNull(String jsonMessage) {
      if (jsonMessage == null) {
         return null;
      } else {
         try {
            return fromJSON(jsonMessage);
         } catch (JsonParseException var2) {
            return null;
         }
      }
   }

   public static Component fromJSONOrString(String message) {
      return fromJSONOrString(message, false);
   }

   public static Component fromJSONOrString(String message, boolean keepNewlines) {
      return fromJSONOrString(message, false, keepNewlines);
   }

   public static Component fromJSONOrString(String message, boolean nullable, boolean keepNewlines) {
      return fromJSONOrString(message, nullable, keepNewlines, Integer.MAX_VALUE, false);
   }

   public static Component fromJSONOrString(String message, boolean nullable, boolean keepNewlines, int maxLength, boolean checkJsonContentLength) {
      if (message == null) {
         message = "";
      }

      if (nullable && message.isEmpty()) {
         return null;
      } else {
         Component component = fromJSONOrNull(message);
         if (component != null) {
            if (checkJsonContentLength) {
               String content = fromComponent(component);
               String trimmedContent = trimMessage(content, maxLength);
               if (content != trimmedContent) {
                  return fromString(trimmedContent, keepNewlines)[0];
               }
            }

            return component;
         } else {
            message = trimMessage(message, maxLength);
            return fromString(message, keepNewlines)[0];
         }
      }
   }

   public static String trimMessage(String message, int maxLength) {
      return message != null && message.length() > maxLength ? message.substring(0, maxLength) : message;
   }

   public static String fromComponent(Component component) {
      if (component == null) {
         return "";
      } else {
         StringBuilder out = new StringBuilder();
         boolean hadFormat = false;

         Component c;
         for(Iterator var3 = component.iterator(); var3.hasNext(); c.getContents().visit((x) -> {
            out.append(x);
            return Optional.empty();
         })) {
            c = (Component)var3.next();
            Style modi = c.getStyle();
            TextColor color = modi.getColor();
            if (c.getContents() != PlainTextContents.EMPTY || color != null) {
               if (color == null) {
                  if (hadFormat) {
                     out.append(ChatColor.RESET);
                     hadFormat = false;
                  }
               } else {
                  if (color.format != null) {
                     out.append(color.format);
                  } else {
                     out.append('§').append("x");
                     char[] var7 = color.serialize().substring(1).toCharArray();
                     int var8 = var7.length;

                     for(int var9 = 0; var9 < var8; ++var9) {
                        char magic = var7[var9];
                        out.append('§').append(magic);
                     }
                  }

                  hadFormat = true;
               }
            }

            if (modi.isBold()) {
               out.append(ChatFormatting.BOLD);
               hadFormat = true;
            }

            if (modi.isItalic()) {
               out.append(ChatFormatting.ITALIC);
               hadFormat = true;
            }

            if (modi.isUnderlined()) {
               out.append(ChatFormatting.UNDERLINE);
               hadFormat = true;
            }

            if (modi.isStrikethrough()) {
               out.append(ChatFormatting.STRIKETHROUGH);
               hadFormat = true;
            }

            if (modi.isObfuscated()) {
               out.append(ChatFormatting.OBFUSCATED);
               hadFormat = true;
            }
         }

         return out.toString();
      }
   }

   public static Component fixComponent(MutableComponent component) {
      Matcher matcher = LINK_PATTERN.matcher("");
      return fixComponent(component, matcher);
   }

   private static Component fixComponent(MutableComponent component, Matcher matcher) {
      if (component.getContents() instanceof PlainTextContents) {
         PlainTextContents text = (PlainTextContents)component.getContents();
         String msg = text.text();
         if (matcher.reset(msg).find()) {
            matcher.reset();
            Style modifier = component.getStyle();
            List<Component> extras = new ArrayList();
            List<Component> extrasOld = new ArrayList(component.getSiblings());
            component = Component.empty();

            int pos;
            for(pos = 0; matcher.find(); pos = matcher.end()) {
               String match = matcher.group();
               if (!match.startsWith("http://") && !match.startsWith("https://")) {
                  match = "http://" + match;
               }

               MutableComponent prev = Component.literal(msg.substring(pos, matcher.start()));
               prev.setStyle(modifier);
               extras.add(prev);
               MutableComponent link = Component.literal(matcher.group());

               try {
                  Style linkModi = modifier.withClickEvent(new ClickEvent.OpenUrl(Util.parseAndValidateUntrustedUri(match)));
                  link.setStyle(linkModi);
               } catch (URISyntaxException var12) {
               }

               extras.add(link);
            }

            MutableComponent prev = Component.literal(msg.substring(pos));
            prev.setStyle(modifier);
            extras.add(prev);
            extras.addAll(extrasOld);
            Iterator var21 = extras.iterator();

            while(var21.hasNext()) {
               Component c = (Component)var21.next();
               component.append(c);
            }
         }
      }

      List<Component> extras = component.getSiblings();

      for(int i = 0; i < extras.size(); ++i) {
         Component comp = (Component)extras.get(i);
         if (comp.getStyle() != null && comp.getStyle().getClickEvent() == null) {
            extras.set(i, fixComponent(comp.copy(), matcher));
         }
      }

      if (component.getContents() instanceof TranslatableContents) {
         Object[] subs = ((TranslatableContents)component.getContents()).getArgs();

         for(int i = 0; i < subs.length; ++i) {
            Object comp = subs[i];
            if (comp instanceof Component) {
               Component c = (Component)comp;
               if (c.getStyle() != null && c.getStyle().getClickEvent() == null) {
                  subs[i] = fixComponent(c.copy(), matcher);
               }
            } else if (comp instanceof String && matcher.reset((String)comp).find()) {
               subs[i] = fixComponent(Component.literal((String)comp), matcher);
            }
         }
      }

      return component;
   }

   private CraftChatMessage() {
   }

   static {
      ImmutableMap.Builder<Character, ChatFormatting> builder = ImmutableMap.builder();
      ChatFormatting[] var1 = ChatFormatting.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ChatFormatting format = var1[var3];
         builder.put(Character.toLowerCase(format.toString().charAt(1)), format);
      }

      formatMap = builder.build();
   }

   private static final class StringMessage {
      private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(" + String.valueOf('§') + "[0-9a-fk-orx])|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf('§') + " \\n]|$))))|(\\n)", 2);
      private static final Pattern INCREMENTAL_PATTERN_KEEP_NEWLINES = Pattern.compile("(" + String.valueOf('§') + "[0-9a-fk-orx])|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf('§') + " ]|$))))", 2);
      private static final Style RESET;
      private final List<Component> list = new ArrayList();
      private MutableComponent currentChatComponent = Component.empty();
      private Style modifier;
      private final Component[] output;
      private int currentIndex;
      private StringBuilder hex;
      private final String message;

      private StringMessage(String message, boolean keepNewlines, boolean plain) {
         this.modifier = Style.EMPTY;
         this.message = message;
         if (message == null) {
            this.output = new Component[]{this.currentChatComponent};
         } else {
            this.list.add(this.currentChatComponent);
            Matcher matcher = (keepNewlines ? INCREMENTAL_PATTERN_KEEP_NEWLINES : INCREMENTAL_PATTERN).matcher(message);
            String match = null;

            boolean needsAdd;
            int groupId;
            for(needsAdd = false; matcher.find(); this.currentIndex = matcher.end(groupId)) {
               groupId = 0;

               do {
                  ++groupId;
               } while((match = matcher.group(groupId)) == null);

               int index = matcher.start(groupId);
               if (index > this.currentIndex) {
                  needsAdd = false;
                  this.appendNewComponent(index);
               }

               switch (groupId) {
                  case 1:
                     char c = match.toLowerCase(Locale.ROOT).charAt(1);
                     ChatFormatting format = (ChatFormatting)CraftChatMessage.formatMap.get(c);
                     if (c == 'x') {
                        this.hex = new StringBuilder("#");
                     } else if (this.hex != null) {
                        this.hex.append(c);
                        if (this.hex.length() == 7) {
                           this.modifier = RESET.withColor((TextColor)TextColor.parseColor(this.hex.toString()).result().get());
                           this.hex = null;
                        }
                     } else if (format.isFormat() && format != ChatFormatting.RESET) {
                        switch (format) {
                           case BOLD -> this.modifier = this.modifier.withBold(Boolean.TRUE);
                           case ITALIC -> this.modifier = this.modifier.withItalic(Boolean.TRUE);
                           case STRIKETHROUGH -> this.modifier = this.modifier.withStrikethrough(Boolean.TRUE);
                           case UNDERLINE -> this.modifier = this.modifier.withUnderlined(Boolean.TRUE);
                           case OBFUSCATED -> this.modifier = this.modifier.withObfuscated(Boolean.TRUE);
                           default -> throw new AssertionError("Unexpected message format");
                        }
                     } else {
                        this.modifier = RESET.withColor(format);
                     }

                     needsAdd = true;
                     break;
                  case 2:
                     if (plain) {
                        this.appendNewComponent(matcher.end(groupId));
                     } else {
                        if (!match.startsWith("http://") && !match.startsWith("https://")) {
                           match = "http://" + match;
                        }

                        try {
                           this.modifier = this.modifier.withClickEvent(new ClickEvent.OpenUrl(Util.parseAndValidateUntrustedUri(match)));
                        } catch (URISyntaxException var12) {
                        }

                        this.appendNewComponent(matcher.end(groupId));
                        this.modifier = this.modifier.withClickEvent((ClickEvent)null);
                     }
                     break;
                  case 3:
                     if (needsAdd) {
                        this.appendNewComponent(index);
                     }

                     this.currentChatComponent = null;
               }
            }

            if (this.currentIndex < message.length() || needsAdd) {
               this.appendNewComponent(message.length());
            }

            this.output = (Component[])this.list.toArray(new Component[this.list.size()]);
         }
      }

      private void appendNewComponent(int index) {
         Component addition = Component.literal(this.message.substring(this.currentIndex, index)).setStyle(this.modifier);
         this.currentIndex = index;
         if (this.currentChatComponent == null) {
            this.currentChatComponent = Component.empty();
            this.list.add(this.currentChatComponent);
         }

         this.currentChatComponent.append(addition);
      }

      private Component[] getOutput() {
         return this.output;
      }

      static {
         RESET = Style.EMPTY.withBold(false).withItalic(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false);
      }
   }

   public static class ChatSerializer {
      private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();

      private ChatSerializer() {
      }

      private static MutableComponent deserialize(JsonElement jsonelement, HolderLookup.Provider holderlookup_a) {
         return (MutableComponent)ComponentSerialization.CODEC.parse(holderlookup_a.createSerializationContext(JsonOps.INSTANCE), jsonelement).getOrThrow(JsonParseException::new);
      }

      private static JsonElement serialize(Component ichatbasecomponent, HolderLookup.Provider holderlookup_a) {
         return (JsonElement)ComponentSerialization.CODEC.encodeStart(holderlookup_a.createSerializationContext(JsonOps.INSTANCE), ichatbasecomponent).getOrThrow(JsonParseException::new);
      }

      public static String toJson(Component ichatbasecomponent, HolderLookup.Provider holderlookup_a) {
         return GSON.toJson(serialize(ichatbasecomponent, holderlookup_a));
      }

      @Nullable
      public static MutableComponent fromJson(String s, HolderLookup.Provider holderlookup_a) {
         JsonElement jsonelement = JsonParser.parseString(s);
         return jsonelement == null ? null : deserialize(jsonelement, holderlookup_a);
      }
   }
}
