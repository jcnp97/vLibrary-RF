package asia.virtualmc.vLibrary.commands;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.commands.integration.BetterModelCommands;
import org.jetbrains.annotations.NotNull;

public class CommandManager {
    private final BetterModelCommands betterModelCommands;

    public CommandManager(@NotNull VLibrary vlib) {
        this.betterModelCommands = new BetterModelCommands(vlib);
    }

    public BetterModelCommands getBetterModelCommands() {
        return betterModelCommands;
    }
}
