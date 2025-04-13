package me.pandy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserDataManager {
    private final PandyNickColor plugin;
    private File userFile;
    private FileConfiguration userData;
    private Map<String, List<String>> playerColors;
    private Map<UUID, Long> telegramUsers; // Добавляем для хранения telegram-users

    public UserDataManager(PandyNickColor plugin) {
        this.plugin = plugin;
        this.playerColors = new HashMap<>();
        this.telegramUsers = new HashMap<>();
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
        telegramUsers.clear();

        // Загрузка цветов игроков (секция "players")
        if (userData.contains("players")) {
            for (String key : userData.getConfigurationSection("players").getKeys(false)) {
                String normalizedKey = key.toLowerCase();
                List<String> colors = userData.getStringList("players." + key);
                playerColors.put(normalizedKey, colors);
            }
        }

        // Загрузка telegram-users
        if (userData.contains("telegram-users")) {
            for (String uuidStr : userData.getConfigurationSection("telegram-users").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Long telegramId = userData.getLong("telegram-users." + uuidStr);
                    telegramUsers.put(uuid, telegramId);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неверный UUID в telegram-users: " + uuidStr);
                }
            }
        }
    }

    public void saveUserData() {
        // Сохранение цветов игроков
        for (Map.Entry<String, List<String>> entry : playerColors.entrySet()) {
            userData.set("players." + entry.getKey(), entry.getValue());
        }

        // Сохранение telegram-users
        for (Map.Entry<UUID, Long> entry : telegramUsers.entrySet()) {
            userData.set("telegram-users." + entry.getKey().toString(), entry.getValue());
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
        userData.set("players." + playerName.toLowerCase(), null);
    }

    // Методы для работы с telegram-users
    public Map<UUID, Long> getTelegramUsers() {
        return telegramUsers;
    }

    public void setTelegramUser(UUID uuid, Long telegramId) {
        if (uuid == null || telegramId == null) {
            plugin.getLogger().warning("uuid or telegramId is null in setTelegramUser");
            return;
        }
        telegramUsers.put(uuid, telegramId);
    }

    public void removeTelegramUser(UUID uuid) {
        if (uuid == null) {
            plugin.getLogger().warning("uuid is null in removeTelegramUser");
            return;
        }
        telegramUsers.remove(uuid);
        userData.set("telegram-users." + uuid.toString(), null);
    }

    public FileConfiguration getConfig() {
        return userData;
    }
}