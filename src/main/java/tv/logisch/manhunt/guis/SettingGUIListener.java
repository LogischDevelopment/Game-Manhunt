package tv.logisch.manhunt.guis;

import org.bukkit.event.Listener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import tv.logisch.manhunt.Manhunt;
import tv.logisch.manhunt.enums.GameState;
import tv.logisch.manhunt.manager.GameManager;

public class SettingGUIListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof org.bukkit.entity.Player player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(Manhunt.instance(), () -> {
            if(player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CRAFTING)) {
                if(SettingGUI.has(player)) {
                    SettingGUI gui = SettingGUI.get(player);
                    gui.close();
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof org.bukkit.entity.Player p)) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals("§b§lManhunt §8» §7Settings")) return;
        if(!SettingGUI.has(p)) return;

        e.setCancelled(true);

        if(!p.hasPermission("logisch.manhunt.admin") && !GameManager.isHost(p.getName())) {
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        NamespacedKey key = new NamespacedKey("manhunt", "setting");
        if(clicked.getItemMeta() == null || !clicked.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        String setting = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if(setting == null) return;

        if(setting.equalsIgnoreCase("release_time")) {
            if(!GameManager.state().equals(GameState.WAITING)) return;
            if(e.isLeftClick() && !e.isShiftClick()) {
                if(GameManager.totalReleaseTime() <= 10) return;
                GameManager.totalReleaseTime(GameManager.totalReleaseTime() - 10);
                GameManager.releaseTime(GameManager.totalReleaseTime());
            } else if(e.isRightClick() && !e.isShiftClick()) {
                GameManager.totalReleaseTime(GameManager.totalReleaseTime() + 10);
                GameManager.releaseTime(GameManager.totalReleaseTime());
            } else if(e.isShiftClick() && e.isRightClick()) {
                GameManager.totalReleaseTime(GameManager.totalReleaseTime() + 60);
                GameManager.releaseTime(GameManager.totalReleaseTime());
            } else if(e.isShiftClick() && e.isLeftClick()) {
                if(GameManager.totalReleaseTime() <= 60) return;
                GameManager.totalReleaseTime(GameManager.totalReleaseTime() - 60);
                GameManager.releaseTime(GameManager.totalReleaseTime());
            }
        } else if(setting.equalsIgnoreCase("keep_inventory")) {
            if(e.isLeftClick()) {
                GameManager.keepInventory(!GameManager.keepInventory());
            }
        } else if(setting.equalsIgnoreCase("bed_bomb")) {
            if(e.isLeftClick()) {
                GameManager.bedBomb(!GameManager.bedBomb());
            }
        } else {
            return;
        }

        SettingGUI.guis.forEach(SettingGUI::update);

    }

}
