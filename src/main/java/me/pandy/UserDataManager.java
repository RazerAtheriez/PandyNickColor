package me.pandy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataManager {
    private final PandyNickColor plugin;
    private File userFile;
    private FileConfiguration userData;
    private Map<String, List<String>> playerColors;

    public UserDataManager(PandyNickColor plugin) {
        this.plugin = plugin;
        this.playerColors = new HashMap<>();
        setupUserData();
    }

    private void setupUserData() {
        userFile = new File(plugin.getDataFolder(), "users.db");
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать users.db: " + e.getMessage());
                e.printStackTrace();
            }
        }
        userData = YamlConfiguration.loadConfiguration(userFile);
        loadUserData();
    }

    public void loadUserData() {
        playerColors.clear();
        for (String key : userData.getKeys(false)) {
            String normalizedKey = key.toLowerCase();
            List<String> colors = userData.getStringList(key);
            playerColors.put(normalizedKey, colors);
        }
    }

    public void saveUserData() {
        for (Map.Entry<String, List<String>> entry : playerColors.entrySet()) {
            userData.set(entry.getKey(), entry.getValue());
        }
        try {
            userData.save(userFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения users.db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getPlayerColors(String playerName) {
        if (playerName == null) {
            plugin.getLogger().warning("playerName is null in getPlayerColors");
            return new ArrayList<>();
        }
        return playerColors.getOrDefault(playerName.toLowerCase(), new ArrayList<>());
    }

    public void setPlayerColors(String playerName, List<String> colors) {
        if (playerName == null) {
            plugin.getLogger().warning("playerName is null in setPlayerColors");
            return;
        }
        playerColors.put(playerName.toLowerCase(), colors);
    }

    public void removePlayerColors(String playerName) {
        if (playerName == null) {
            plugin.getLogger().warning("playerName is null in removePlayerColors");
            return;
        }
        playerColors.remove(playerName.toLowerCase());
    }
}