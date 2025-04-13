package me.pandy.api;

import me.pandy.PandyNickColor;
import me.pandy.TabManager;
import me.pandy.UserDataManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PandyNickColorAPI {
    private final PandyNickColor plugin;
    private final UserDataManager userDataManager;
    private final TabManager tabManager;

    public PandyNickColorAPI(PandyNickColor plugin, UserDataManager userDataManager, TabManager tabManager) {
        this.plugin = plugin;
        this.userDataManager = userDataManager;
        this.tabManager = tabManager;
    }

    public void setNickColor(OfflinePlayer offlinePlayer, String colorKey) {
        if (offlinePlayer == null || colorKey == null) {
            plugin.getLogger().warning("offlinePlayer or colorKey is null in setNickColor");
            return;
        }
        ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
        if (colors != null && colors.contains(colorKey)) {
            List<String> playerColors = userDataManager.getPlayerColors(offlinePlayer.getName());
            if (!playerColors.contains(colorKey)) {
                playerColors.add(colorKey);
                userDataManager.setPlayerColors(offlinePlayer.getName(), playerColors);
                userDataManager.saveUserData();
            }
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                tabManager.applyNickColor(player, colorKey, plugin);
            }
        } else {
            plugin.getLogger().warning("Цвет " + colorKey + " не найден в config.yml");
        }
    }

    public void resetNickColor(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            plugin.getLogger().warning("offlinePlayer is null in resetNickColor");
            return;
        }
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            tabManager.resetNickColor(player);
        }
    }

    public boolean hasNickColor(OfflinePlayer offlinePlayer, String colorKey) {
        if (offlinePlayer == null || colorKey == null) {
            plugin.getLogger().warning("offlinePlayer or colorKey is null in hasNickColor");
            return false;
        }
        List<String> playerColors = userDataManager.getPlayerColors(offlinePlayer.getName());
        return playerColors.contains(colorKey);
    }

    public List<String> getAvailableColors(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) {
            return new ArrayList<>();
        }
        return userDataManager.getPlayerColors(offlinePlayer.getName());
    }

    public List<String> getAllColors() {
        ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
        if (colors == null) {
            plugin.getLogger().warning("colors section is null in config.yml");
            return new ArrayList<>();
        }
        return new ArrayList<>(colors.getKeys(false));
    }

    public String getColorDescription(String colorKey) {
        if (colorKey == null) {
            plugin.getLogger().warning("colorKey is null in getColorDescription");
            return null;
        }
        ConfigurationSection colorSection = plugin.getConfig().getConfigurationSection("colors." + colorKey);
        return colorSection != null ? colorSection.getString("description") : null;
    }

    public void removeNickColor(OfflinePlayer offlinePlayer, String colorKey) {
        if (offlinePlayer == null || colorKey == null) {
            plugin.getLogger().warning("offlinePlayer or colorKey is null in removeNickColor");
            return;
        }
        List<String> playerColors = userDataManager.getPlayerColors(offlinePlayer.getName());
        if (playerColors.remove(colorKey)) {
            userDataManager.setPlayerColors(offlinePlayer.getName(), playerColors);
            userDataManager.saveUserData();
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                if (playerColors.isEmpty()) {
                    tabManager.resetNickColor(player);
                } else {
                    tabManager.applyNickColor(player, playerColors.get(0), plugin);
                }
            }
        }
    }
}