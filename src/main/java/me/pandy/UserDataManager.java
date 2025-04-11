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
                e.printStackTrace();
            }
        }
        userData = YamlConfiguration.loadConfiguration(userFile);
    }

    public void loadUserData() {
        playerColors.clear();
        for (String key : userData.getKeys(false)) {
            playerColors.put(key, userData.getStringList(key));
        }
    }

    public void saveUserData() {
        for (Map.Entry<String, List<String>> entry : playerColors.entrySet()) {
            userData.set(entry.getKey(), entry.getValue());
        }
        try {
            userData.save(userFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPlayerColors(String playerName) {
        return playerColors.getOrDefault(playerName, new ArrayList<>());
    }

    public void setPlayerColors(String playerName, List<String> colors) {
        playerColors.put(playerName, colors);
    }

    public void removePlayerColors(String playerName) {
        playerColors.remove(playerName);
    }
}