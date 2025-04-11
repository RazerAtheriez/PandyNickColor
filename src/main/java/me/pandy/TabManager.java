package me.pandy;

import org.bukkit.entity.Player;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.TabListFormatManager;

public class TabManager {

    public void applyNickColor(Player player, String colorKey, PandyNickColor plugin) {
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabListFormatManager formatManager = TabAPI.getInstance().getTabListFormatManager();
            if (formatManager != null) {
                String startColor = plugin.getConfig().getConfigurationSection("colors." + colorKey).getString("startColor");
                String endColor = plugin.getConfig().getConfigurationSection("colors." + colorKey).getString("endColor");
                String formattedName = startColor + player.getName() + endColor;
                formatManager.setName(tabPlayer, formattedName);
            }
        }
    }

    public void resetNickColor(Player player) {
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer != null) {
            TabListFormatManager formatManager = TabAPI.getInstance().getTabListFormatManager();
            if (formatManager != null) {
                formatManager.setName(tabPlayer, null);
            }
        }
    }
}