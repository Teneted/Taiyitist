package org.bukkit.craftbukkit.v1_21_R5.command;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.craftbukkit.v1_21_R5.conversations.ConversationTracker;

public class CraftConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {
   protected final ConversationTracker conversationTracker = new ConversationTracker();

   protected CraftConsoleCommandSender() {
   }

   public void sendMessage(String message) {
      this.sendRawMessage(message);
   }

   public void sendRawMessage(String message) {
      System.out.println(ChatColor.stripColor(message));
   }

   public void sendRawMessage(UUID sender, String message) {
      this.sendRawMessage(message);
   }

   public void sendMessage(String... messages) {
      String[] var2 = messages;
      int var3 = messages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String message = var2[var4];
         this.sendMessage(message);
      }

   }

   public String getName() {
      return "CONSOLE";
   }

   public boolean isOp() {
      return true;
   }

   public void setOp(boolean value) {
      throw new UnsupportedOperationException("Cannot change operator status of server console");
   }

   public boolean beginConversation(Conversation conversation) {
      return this.conversationTracker.beginConversation(conversation);
   }

   public void abandonConversation(Conversation conversation) {
      this.conversationTracker.abandonConversation(conversation, new ConversationAbandonedEvent(conversation, new ManuallyAbandonedConversationCanceller()));
   }

   public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
      this.conversationTracker.abandonConversation(conversation, details);
   }

   public void acceptConversationInput(String input) {
      this.conversationTracker.acceptConversationInput(input);
   }

   public boolean isConversing() {
      return this.conversationTracker.isConversing();
   }
}
