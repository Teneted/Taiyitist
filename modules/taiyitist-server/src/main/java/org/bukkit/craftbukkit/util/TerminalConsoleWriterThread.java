package org.bukkit.craftbukkit.util;

import com.mojang.logging.LogQueues;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import org.bukkit.craftbukkit.Main;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Erase;

public class TerminalConsoleWriterThread extends Thread {
   private final ResourceBundle bundle = ResourceBundle.getBundle(CandidateListCompletionHandler.class.getName(), Locale.getDefault());
   private final ConsoleReader reader;
   private final OutputStream output;
   private volatile int completion = -1;

   public TerminalConsoleWriterThread(OutputStream output, ConsoleReader reader) {
      super("TerminalConsoleWriter");
      this.output = output;
      this.reader = reader;
      this.setDaemon(true);
   }

   public void run() {
      while(true) {
         String message = LogQueues.getNextLogEvent("TerminalConsole");
         if (message != null) {
            try {
               if (Main.useJline) {
                  ConsoleReader var10000 = this.reader;
                  Ansi var10001 = Ansi.ansi();
                  var10000.print(var10001.eraseLine(Erase.ALL).toString() + "\r");
                  this.reader.flush();
                  this.output.write(message.getBytes());
                  this.output.flush();

                  try {
                     this.reader.drawLine();
                  } catch (Throwable var3) {
                     this.reader.getCursorBuffer().clear();
                  }

                  if (this.completion > -1) {
                     this.reader.print(String.format(this.bundle.getString("DISPLAY_CANDIDATES"), this.completion));
                  }

                  this.reader.flush();
               } else {
                  this.output.write(message.getBytes());
                  this.output.flush();
               }
            } catch (IOException var4) {
               IOException ex = var4;
               Logger.getLogger(TerminalConsoleWriterThread.class.getName()).log(Level.SEVERE, (String)null, ex);
            }
         }
      }
   }

   void setCompletion(int completion) {
      this.completion = completion;
   }
}
