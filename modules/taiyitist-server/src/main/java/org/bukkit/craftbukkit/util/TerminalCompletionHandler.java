package org.bukkit.craftbukkit.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jline.console.ConsoleReader;
import jline.console.completer.CompletionHandler;

public class TerminalCompletionHandler implements CompletionHandler {
   private final TerminalConsoleWriterThread writerThread;
   private final CompletionHandler delegate;

   public TerminalCompletionHandler(TerminalConsoleWriterThread writerThread, CompletionHandler delegate) {
      this.writerThread = writerThread;
      this.delegate = delegate;
   }

   public boolean complete(ConsoleReader reader, List<CharSequence> candidates, int position) throws IOException {
      if (candidates.size() <= reader.getAutoprintThreshold()) {
         return this.delegate.complete(reader, candidates, position);
      } else {
         Set<CharSequence> distinct = new HashSet(candidates);
         if (distinct.size() <= reader.getAutoprintThreshold()) {
            return this.delegate.complete(reader, candidates, position);
         } else {
            this.writerThread.setCompletion(distinct.size());
            boolean result = this.delegate.complete(reader, candidates, position);
            this.writerThread.setCompletion(-1);
            reader.drawLine();
            reader.flush();
            return result;
         }
      }
   }
}
