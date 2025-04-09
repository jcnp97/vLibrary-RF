package asia.virtualmc.vLibrary.integrations;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLibrary.utilities.messages.MessageUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultEconomy {
    private final VLibrary vlib;
    private static Economy economy;

    public VaultEconomy(VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        if (vlib.getServer().getPluginManager().getPlugin("Vault") == null) {
            vlib.getLogger().severe("Vault plugin not found! Disabling vLibrary..");
            disablePlugin();
        }

        ConsoleUtils.sendMessage("Vault found! Attempting to get economy registration..");
        RegisteredServiceProvider<Economy> rspEconomy = vlib.getServer().getServicesManager().getRegistration(Economy.class);

        if (rspEconomy == null) {
            vlib.getLogger().severe("No economy provider was registered with Vault!");
            disablePlugin();
        }

        assert rspEconomy != null;
        economy = rspEconomy.getProvider();
        ConsoleUtils.sendMessage("Successfully hooked into: " + economy.getName());
    }

    /**
     * Adds money to a player's balance
     *
     * @param player The player to add money to
     * @param amount The amount to add
     * @return The new balance
     */
    public double addMoney(@NotNull Player player, double amount) {
        if (amount <= 0) {
            return economy.getBalance(player);
        }

        double rounded = DigitUtils.getPreciseValue(amount, 2);
        economy.depositPlayer(player, rounded);
        double newBalance = economy.getBalance(player);

        MessageUtils.sendPlayerMessage(player, "You have received $" + rounded + ". You now have $" + newBalance + ".");

        return newBalance;
    }

    /**
     * Removes money from a player's balance if they have enough
     *
     * @param player The player to remove money from
     * @param amount The amount to remove
     * @return true if the transaction was successful, false if the player doesn't have enough money
     */
    public boolean removeMoney(@NotNull Player player, double amount) {
        if (amount <= 0) {
            return true;
        }

        if (amount > economy.getBalance(player)) {
            return false;
        }

        double rounded = DigitUtils.getPreciseValue(amount, 2);
        economy.withdrawPlayer(player, rounded);
        double newBalance = economy.getBalance(player);

        MessageUtils.sendPlayerMessage(player, "<gold>$" + rounded +
                " <red>was taken from your balance. You now have <green>$" + newBalance + ".");

        return true;
    }

    /**
     * Applies a tax deduction to the player
     *
     * @param player The player to tax
     * @param taxRate The tax rate (0.0 to 1.0)
     * @param amount The amount to base the tax on
     * @return The amount of tax paid
     * @throws IllegalArgumentException if the tax rate is invalid
     */
    public double applyTax(@NotNull Player player, double taxRate, double amount) {
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Tax rate must be between 0.0 and 1.0");
        }

        if (amount <= 0) {
            return 0;
        }

        double taxPaid = Math.round(amount * taxRate * 100.0) / 100.0;

        if (taxPaid <= 0) {
            return 0;
        }

        if (taxPaid > economy.getBalance(player)) {
            return 0; // Can't pay tax if player doesn't have enough money
        }

        economy.withdrawPlayer(player, taxPaid);
        MessageUtils.sendPlayerMessage(player, "<red>You have paid <gold>$" + taxPaid +
                " <red>in taxes.");

        return taxPaid;
    }

    /**
     * Gets the player's current balance
     *
     * @param player The player to check
     * @return The player's balance
     */
    public double getBalance(@NotNull Player player) {
        return economy.getBalance(player);
    }

    public static Economy getEconomy() {
        return economy;
    }

    private void disablePlugin() {
        vlib.getServer().getPluginManager().disablePlugin(vlib);
    }
}
