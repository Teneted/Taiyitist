package org.celestial_artistry.taiyitist.injection.network.chat;

import java.util.stream.Stream;
import net.minecraft.network.chat.Component;

public interface InjectionComponent {

    default Stream<Component> bridge$stream() {
        return null;
    }
}
