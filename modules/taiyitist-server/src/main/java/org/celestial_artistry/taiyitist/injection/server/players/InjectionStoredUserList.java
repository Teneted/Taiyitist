package org.celestial_artistry.taiyitist.injection.server.players;

import java.util.Collection;
import net.minecraft.server.players.StoredUserEntry;

public interface InjectionStoredUserList<K, V extends StoredUserEntry<K>> {


   default Collection<V> getValues() {
       return null;
   }
}
