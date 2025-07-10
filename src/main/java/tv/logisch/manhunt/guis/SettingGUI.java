package tv.logisch.manhunt.guis;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.manhunt.manager.GameManager;
import tv.logisch.manhunt.utils.Format;

import java.util.ArrayList;
import java.util.List;

public class SettingGUI {

    public static List<SettingGUI> guis = new ArrayList<>();
    public static SettingGUI get(Player player) {
        for(SettingGUI gui : guis) {
            if(gui.p.equals(player)) {
                return gui;
            }
        }
        return new SettingGUI(player);
    }
    public static boolean has(Player player) {
        for(SettingGUI gui : guis) {
            if(gui.p.equals(player)) {
                return true;
            }
        }
        return false;
    }

    @Getter @Accessors(fluent = true)
    private final Player p;

    private Inventory inventory;

    private SettingGUI(Player player) {
        this.p = player;
        guis.add(this);
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this.p, 45, Component.text("§b§lManhunt §8» §7Settings"));
        update();
    }

    public void update() {
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        stack.editMeta(m -> m.setHideTooltip(true));
        for(int i = 0; i < 9; i++) {
            this.inventory.setItem(i, stack);
        }
        for(int i = 36; i < 45; i++) {
            this.inventory.setItem(i, stack);
        }

        /* BED BOMB */
        stack = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.bedBomb()) stack = new ItemStack(Material.GREEN_DYE, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Bed bomb"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + (GameManager.bedBomb() ? "Enabled" : "Disabled")));
            lore.add(Component.text("§fL-click§8: §7Toggle bed bomb"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("manhunt", "setting"), PersistentDataType.STRING, "bed_bomb");
        });
        this.inventory.setItem(20, stack);

        /* RELEASE TIME */
        stack = new ItemStack(Material.NETHERITE_SWORD, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Release Time"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + Format.time(GameManager.releaseTime())));
            lore.add(Component.text("§fL-click§8: §7Decrease by 10 second"));
            lore.add(Component.text("§fR-click§8: §7Increase by 10 second"));
            lore.add(Component.text("§fShift + R-click§8: §7Increase by 60 seconds"));
            lore.add(Component.text("§fShift + L-click§8: §7Decrease by 60 seconds"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("manhunt", "setting"), PersistentDataType.STRING, "release_time");
        });
        this.inventory.setItem(22, stack);

        /* KEEP INVENTORY */
        stack = new ItemStack(Material.RED_DYE, 1);
        if(GameManager.keepInventory()) stack = new ItemStack(Material.GREEN_DYE, 1);
        stack.editMeta(meta -> {
            meta.displayName(Component.text("§8» §7Keep Inventory"));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Current: §f" + (GameManager.keepInventory() ? "Enabled" : "Disabled")));
            lore.add(Component.text("§fL-click§8: §7Toggle keep inventory"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(new NamespacedKey("manhunt", "setting"), PersistentDataType.STRING, "keep_inventory");
        });
        this.inventory.setItem(24, stack);

        this.p.openInventory(this.inventory);

    }

    public void close() {
        guis.remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingGUI that)) return false;
        return p.equals(that.p);
    }

    @Override
    public int hashCode() {
        return ("manhunt_gui_" + p.getUniqueId().toString()).hashCode();
    }

}
