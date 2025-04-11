package me.pandy.api;

import me.pandy.PandyNickColor;
import me.pandy.TabManager;
import me.pandy.UserDataManager;
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

    public void setNickColor(Player player, String colorKey) {
        if (player == null || colorKey == null) {
            plugin.getLogger().warning("player or colorKey is null in setNickColor");
            return;
        }
        ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
        if (colors != null && colors.contains(colorKey)) {
            List<String> playerColors = userDataManager.getPlayerColors(player.getName());
            if (!playerColors.contains(colorKey)) {
                playerColors.add(colorKey);
                userDataManager.setPlayerColors(player.getName(), playerColors);
                userDataManager.saveUserData();
            }
            tabManager.applyNickColor(player, colorKey, plugin);
        } else {
            plugin.getLogger().warning("Цвет " + colorKey + " не найден в config.yml");
        }
    }

    public void resetNickColor(Player player) {
        if (player == null) {
            plugin.getLogger().warning("player is null in resetNickColor");
            return;
        }
        tabManager.resetNickColor(player);
    }

    public boolean hasNickColor(Player player, String colorKey) {
        if (player == null || colorKey == null) {
            plugin.getLogger().warning("player or colorKey is null in hasNickColor");
            return false;
        }
        List<String> playerColors = userDataManager.getPlayerColors(player.getName());
        return playerColors.contains(colorKey);
    }

    public List<String> getAvailableColors(Player player) {
        if (player == null) {
            plugin.getLogger().warning("player is null in getAvailableColors");
            return new ArrayList<>();
        }
        List<String> colors = userDataManager.getPlayerColors(player.getName());
        plugin.getLogger().info("API: Возвращены цвета для игрока " + player.getName() + ": " + colors);
        return colors;
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

    public void removeNickColor(Player player, String colorKey) {
        if (player == null || colorKey == null) {
            plugin.getLogger().warning("player or colorKey is null in removeNickColor");
            return;
        }
        List<String> playerColors = userDataManager.getPlayerColors(player.getName());
        if (playerColors.remove(colorKey)) {
            userDataManager.setPlayerColors(player.getName(), playerColors);
            userDataManager.saveUserData();
            if (playerColors.isEmpty()) {
                tabManager.resetNickColor(player);
            } else {
                tabManager.applyNickColor(player, playerColors.get(0), plugin);
            }
        }
    }
}