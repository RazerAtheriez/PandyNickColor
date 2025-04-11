package me.pandy;

import me.pandy.api.PandyNickColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PandyNickColor extends JavaPlugin {

    private FileConfiguration messages;
    private File messagesFile;
    private UserDataManager userDataManager;
    private TabManager tabManager;
    private CommandHandler commandHandler;
    private PandyNickColorAPI api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupMessages();
        userDataManager = new UserDataManager(this);
        tabManager = new TabManager();
        commandHandler = new CommandHandler(this, userDataManager, tabManager);
        api = new PandyNickColorAPI(this);

        getServer().getPluginManager().registerEvents(commandHandler, this);

        Objects.requireNonNull(getCommand("nickcolor")).setExecutor(commandHandler);
        Objects.requireNonNull(getCommand("nickcolor")).setTabCompleter(commandHandler);
        Objects.requireNonNull(getCommand("tabcolor")).setExecutor(commandHandler);
        Objects.requireNonNull(getCommand("tabcolor")).setTabCompleter(commandHandler);
        Objects.requireNonNull(getCommand("pandynickcolor")).setExecutor(commandHandler);

        userDataManager.loadUserData();
    }

    private void setupMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadConfigs() {
        reloadConfig();
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        userDataManager.loadUserData();
    }

    @Override
    public FileConfiguration getConfig() {
        return super.getConfig();
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public PandyNickColorAPI getAPI() {
        return api;
    }

    @Override
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            FileConfiguration config = getConfig();
            config.createSection("colors");
            config.getConfigurationSection("colors").set("scalk.startColor", "<#33E6F1>");
            config.getConfigurationSection("colors").set("scalk.endColor", "</#FFFFFF>");
            config.getConfigurationSection("colors").set("scalk.description", "Скалковый");
            saveConfig();
        }
    }
}