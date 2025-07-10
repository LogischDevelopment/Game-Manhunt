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
        if(!PlainTextComponentSerializer.plainText().serialize(e.getView().title()).equals("§b§lOBR §8» §7Settings")) return;
        if(!SettingGUI.has(p)) return;

        e.setCancelled(true);
        ItemStack clicked = e.getCurrentItem();
        NamespacedKey key = new NamespacedKey("obr", "setting");
        if(clicked.getItemMeta() == null || !clicked.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            return;
        }

        String setting = clicked.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if(setting == null) return;

        if(setting.equalsIgnoreCase("game_duration")) {
            if(e.isLeftClick() && !e.isShiftClick()) {
                if(GameManager.time() <= 5 * 60) return;
                GameManager.time(GameManager.time() - 5 * 60);
            } else if(e.isRightClick() && !e.isShiftClick()) {
                GameManager.time(GameManager.time() + 5 * 60);
            } else if(e.isShiftClick() && e.isRightClick()) {
                GameManager.time(GameManager.time() + 10 * 60);
            } else if(e.isShiftClick() && e.isLeftClick()) {
                if(GameManager.time() <= 10 * 60) return;
                GameManager.time(GameManager.time() - 10 * 60);
            }
        } else if(setting.equalsIgnoreCase("release_time")) {
            if(e.isLeftClick() && !e.isShiftClick()) {
                if(GameManager.releaseTime() <= 10) return;
                GameManager.releaseTime(GameManager.releaseTime() - 10);
            } else if(e.isRightClick() && !e.isShiftClick()) {
                GameManager.releaseTime(GameManager.releaseTime() + 10);
            } else if(e.isShiftClick() && e.isRightClick()) {
                GameManager.releaseTime(GameManager.releaseTime() + 60);
            } else if(e.isShiftClick() && e.isLeftClick()) {
                if(GameManager.releaseTime() <= 60) return;
                GameManager.releaseTime(GameManager.releaseTime() - 60);
            }
        } else if(setting.equalsIgnoreCase("keep_inventory")) {
            if(e.isLeftClick()) {
                GameManager.keepInventory(!GameManager.keepInventory());
            }
        } else {
            return;
        }

        SettingGUI.guis.forEach(SettingGUI::update);

    }

}
