package asia.virtualmc.vLibrary.utilities.minecraft;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class GUIUtils {
    public static ItemStack createButton(Material material, String displayName, int modelData) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setCustomModelData(modelData);
            button.setItemMeta(meta);
        }
        return button;
    }

    public static ItemStack createLegacyButton(Material material, String displayName, int modelData,
                                               List<String> lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setCustomModelData(modelData);
            meta.setLore(lore);
            button.setItemMeta(meta);
        }
        return button;
    }

    public static ItemStack createModernButton(Material material, String displayName, int modelData,
                                               List<String> lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            MiniMessage miniMessage = MiniMessage.miniMessage();

            Component displayNameComponent = miniMessage.deserialize("<!i>" + displayName);
            meta.displayName(displayNameComponent);

            List<Component> loreComponents = lore.stream()
                    .map(line -> miniMessage.deserialize("<!i>" + line))
                    .collect(Collectors.toList());
            meta.lore(loreComponents);

            meta.setCustomModelData(modelData);
            button.setItemMeta(meta);
        }
        return button;
    }

    public static ChestGui getDisplay(String displayName, List<ItemStack> items) {
        ChestGui gui = new ChestGui(6, displayName);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        OutlinePane pane = new OutlinePane(9, 6);

        for (ItemStack item : items) {
            GuiItem guiItem = new GuiItem(item);
            pane.addItem(guiItem);
        }

        gui.addPane(pane);
        return gui;
    }
}
