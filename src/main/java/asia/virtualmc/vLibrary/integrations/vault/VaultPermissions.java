package asia.virtualmc.vLibrary.integrations.vault;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.permission.Permission;
import org.jetbrains.annotations.NotNull;

public class VaultPermissions {
    private final VLibrary vlib;
    private static Permission permission;
    private static VaultPermissions vaultPermissions;

    public VaultPermissions(VLibrary vlib) {
        vaultPermissions = this;
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        if (vlib.getServer().getPluginManager().getPlugin("Vault") == null) {
            ConsoleUtils.severe("Vault plugin not found! Disabling vLibrary..");
            disablePlugin();
        }

        ConsoleUtils.warning("Vault found! Attempting to get permission registration..");
        RegisteredServiceProvider<Permission> rspPermission = vlib.getServer().getServicesManager().getRegistration(Permission.class);

        if (rspPermission == null) {
            ConsoleUtils.severe("No permission provider was registered with Vault!");
            disablePlugin();
        }

        assert rspPermission != null;
        permission = rspPermission.getProvider();
        ConsoleUtils.info("Successfully hooked into: " + permission.getName());
    }

    /**
     * Adds a permission to a player
     *
     * @param player The player to add the permission to
     * @param permissionNode The permission node to add
     */
    public void addPermission(@NotNull Player player, @NotNull String permissionNode) {
        if (permissionNode.isEmpty()) {
            return;
        }

        if (permission.playerHas(player, permissionNode)) {
            ConsoleUtils.severe(player.getName() + " already has " + permissionNode + " permission!");
            return;
        }

        boolean result = permission.playerAdd(player, permissionNode);
        if (result) {
            ConsoleUtils.info("Added " + permissionNode + " to " + player.getName());
        }
    }

    /**
     * Adds a permission to a player in a specific world
     *
     * @param player The player to add the permission to
     * @param world The world name where the permission applies
     * @param permissionNode The permission node to add
     */
    public void addPermission(@NotNull Player player, @NotNull String world, @NotNull String permissionNode) {
        if (permissionNode.isEmpty() || world.isEmpty()) {
            ConsoleUtils.severe("Invalid world name or permission node: " + world + ", " + permissionNode);
            return;
        }

        // Check if player already has the permission in this world
        if (permission.playerHas(world, player, permissionNode)) {
            ConsoleUtils.severe(player.getName() + " already has " + permissionNode + " permission!");
            return;
        }

        boolean result = permission.playerAdd(world, player, permissionNode);
        if (result) {
            ConsoleUtils.info("Added " + permissionNode + " to " + player.getName() + " with world parameter: " + world);
        }
    }

    /**
     * Removes a permission from a player
     *
     * @param player The player to remove the permission from
     * @param permissionNode The permission node to remove
     */
    public void removePermission(@NotNull Player player, @NotNull String permissionNode) {
        if (permissionNode.isEmpty()) {
            ConsoleUtils.severe("Invalid permission node: " + permissionNode);
            return;
        }

        // Check if player even has the permission
        if (!permission.playerHas(player, permissionNode)) {
            ConsoleUtils.severe(player.getName() + " does not have " + permissionNode + " permission!");
            return;
        }

        boolean result = permission.playerRemove(player, permissionNode);
        if (result) {
            ConsoleUtils.info("Removed " + permissionNode + " from " + player.getName());
        }
    }

    /**
     * Removes a permission from a player in a specific world
     *
     * @param player The player to remove the permission from
     * @param world The world name where the permission applies
     * @param permissionNode The permission node to remove
     */
    public void removePermission(@NotNull Player player, @NotNull String world, @NotNull String permissionNode) {
        if (permissionNode.isEmpty() || world.isEmpty()) {
            ConsoleUtils.severe("Invalid world name or permission node: " + world + ", " + permissionNode);
            return;
        }

        // Check if player even has the permission in this world
        if (!permission.playerHas(world, player, permissionNode)) {
            ConsoleUtils.severe(player.getName() + " does not have " + permissionNode + " permission!");
            return;
        }

        boolean result = permission.playerRemove(world, player, permissionNode);
        if (result) {
            ConsoleUtils.info("Removed " + permissionNode + " from " + player.getName() + " with world parameter: " + world);
        }
    }

    /**
     * Checks if a player has a specific permission
     *
     * @param player The player to check
     * @param permissionNode The permission node to check
     * @return true if the player has the permission, false otherwise
     */
    public boolean hasPermission(@NotNull Player player, @NotNull String permissionNode) {
        if (permissionNode.isEmpty()) {
            return false;
        }

        return permission.playerHas(player, permissionNode);
    }

    /**
     * Checks if a player has a specific permission in a specific world
     *
     * @param player The player to check
     * @param world The world name to check in
     * @param permissionNode The permission node to check
     * @return true if the player has the permission in the specified world, false otherwise
     */
    public boolean hasPermission(@NotNull Player player, @NotNull String world, @NotNull String permissionNode) {
        if (permissionNode.isEmpty() || world.isEmpty()) {
            return false;
        }

        return permission.playerHas(world, player, permissionNode);
    }

    public static Permission getPermission() {
        return permission;
    }

    public static VaultPermissions get() { return vaultPermissions; }

    private void disablePlugin() {
        vlib.getServer().getPluginManager().disablePlugin(vlib);
    }
}
