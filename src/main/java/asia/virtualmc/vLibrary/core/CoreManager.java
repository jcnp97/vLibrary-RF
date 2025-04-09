package asia.virtualmc.vLibrary.core;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.core.files.TexturePathReplacer;
import org.jetbrains.annotations.NotNull;

public class CoreManager {
    private final VLibrary vlib;
    private final TexturePathReplacer texturePathReplacer;

    public CoreManager(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        this.texturePathReplacer = new TexturePathReplacer(vlib);
    }
}
