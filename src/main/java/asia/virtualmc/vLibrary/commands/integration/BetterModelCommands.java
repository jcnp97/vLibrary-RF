package asia.virtualmc.vLibrary.commands.integration;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.integrations.bettermodel.BetterModelUtils;
import asia.virtualmc.vLibrary.utilities.minecraft.EntityUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BetterModelCommands {

    public BetterModelCommands(@NotNull VLibrary vlib) {
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("vlib")
                .withSubcommand(spawnModel())
                .withSubcommand(changeModel())
                .register();
    }

    private CommandAPICommand spawnModel() {
        return new CommandAPICommand("spawn_bm")
                .withPermission("cozyvanilla.rank.admin")
                .withArguments(new StringArgument("modelName"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        String modelName = (String) args.get("modelName");

                        Block targetBlock = player.getTargetBlockExact(10);
                        if (targetBlock == null) {
                            player.sendMessage("§cNo block in sight to place the model on.");
                            return;
                        }

                        Location location = targetBlock.getLocation().add(0.5, 1.0, 0.5);
                        Entity result = BetterModelUtils.spawn(location, modelName);

                        if (result == null) {
                            player.sendMessage("§cFailed to spawn model. Model '" + modelName + "' does not exist.");
                        } else {
                            player.sendMessage("§aSpawned model: §e" + modelName);
                        }
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }

    private CommandAPICommand changeModel() {
        return new CommandAPICommand("change_bm")
                .withPermission("cozyvanilla.rank.admin")
                .withArguments(new StringArgument("modelName"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        String modelName = (String) args.get("modelName");

                        Entity target = EntityUtils.getTarget(player, 10, EntityType.ITEM_DISPLAY);
                        if (target == null) {
                            player.sendMessage("§cNo entity in sight or out of range.");
                            return;
                        }

                        boolean success = BetterModelUtils.change(target, modelName);
                        if (!success) {
                            player.sendMessage("§cFailed to apply model. Model '" + modelName + "' does not exist.");
                        } else {
                            player.sendMessage("§aChanged model to: §e" + modelName);
                        }
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }
}
