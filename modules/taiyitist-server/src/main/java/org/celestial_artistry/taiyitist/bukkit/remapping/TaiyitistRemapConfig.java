package org.celestial_artistry.taiyitist.bukkit.remapping;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/*
 * Used to record transformation detail for specific ClassLoaders.
 */
public record TaiyitistRemapConfig(boolean remap) {
    public static final TaiyitistRemapConfig PLUGIN = new TaiyitistRemapConfig(true);

    public TaiyitistRemapConfig copy() {
        return new TaiyitistRemapConfig(remap);
    }

    public int write(DataOutput output) throws IOException {
        output.writeBoolean(remap);
        return 1;
    }

    public static TaiyitistRemapConfig read(DataInput input) throws IOException {
        return new TaiyitistRemapConfig(input.readBoolean());
    }
}