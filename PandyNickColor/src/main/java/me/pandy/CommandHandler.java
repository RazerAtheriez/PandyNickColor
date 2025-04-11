package me.pandy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter, Listener {

    private final PandyNickColor plugin;
    private final UserDataManager userDataManager;
    private final TabManager tabManager;

    public CommandHandler(PandyNickColor plugin, UserDataManager userDataManager, TabManager tabManager) {
        this.plugin = plugin;
        this.userDataManager = userDataManager;
        this.tabManager = tabManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nickcolor")) {
            if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("nickcolor.color")) {
                sender.sendMessage(getMessage("no-permission"));
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage(getMessage("nickcolor-usage"));
                return true;
            }

            String action = args[0];
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);

            if (action.equalsIgnoreCase("clear")) {
                userDataManager.removePlayerColors(targetName);
                userDataManager.saveUserData();
                if (target != null) {
                    tabManager.resetNickColor(target);
                }
                sender.sendMessage(getMessage("color-cleared").replace("%player%", targetName));
                return true;
            }

            ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
            if (colors == null || !colors.contains(action)) {
                sender.sendMessage(getMessage("color-not-found"));
                return true;
            }

            List<String> playerPermittedColors = userDataManager.getPlayerColors(targetName);
            if (!playerPermittedColors.contains(action)) {
                playerPermittedColors.add(action);
                userDataManager.setPlayerColors(targetName, playerPermittedColors);
                userDataManager.saveUserData();
            }

            sender.sendMessage(getMessage("color-assigned").replace("%color%", action).replace("%player%", targetName));
            if (target != null) {
                tabManager.applyNickColor(target, action, plugin);
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("tabcolor")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getMessage("players-only"));
                return true;
            }

            Player player = (Player) sender;
            if (args.length != 1) {
                sender.sendMessage(getMessage("tabcolor-usage"));
                return true;
            }

            String colorDesc = args[0];

            if (colorDesc.equalsIgnoreCase("clear")) {
                tabManager.resetNickColor(player);
                player.sendMessage(getMessage("color-reset"));
                return true;
            }

            List<String> permittedColors = userDataManager.getPlayerColors(player.getName());
            String colorKey = null;
            ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
            for (String key : colors.getKeys(false)) {
                if (colors.getString(key + ".description").equalsIgnoreCase(colorDesc)) {
                    colorKey = key;
                    break;
                }
            }

            if (colorKey == null || !permittedColors.contains(colorKey)) {
                player.sendMessage(getMessage("no-access-to-color"));
                return true;
            }

            tabManager.applyNickColor(player, colorKey, plugin);
            player.sendMessage(getMessage("color-changed").replace("%color%", colorDesc));
            return true;
        }

        if (command.getName().equalsIgnoreCase("pandynickcolor")) {
            if (!sender.hasPermission("nickcolor.reload")) {
                sender.sendMessage(getMessage("no-permission"));
                return true;
            }

            if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(getMessage("reload-usage"));
                return true;
            }

            plugin.reloadConfigs();
            sender.sendMessage(getMessage("reload-success"));
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> permittedColors = userDataManager.getPlayerColors(player.getName());
        if (permittedColors != null && !permittedColors.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> tabManager.applyNickColor(player, permittedColors.get(0), plugin), 2L);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("nickcolor")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>(plugin.getConfig().getConfigurationSection("colors").getKeys(false));
                completions.add("clear");
                return completions;
            }
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
            }
        }

        if (command.getName().equalsIgnoreCase("tabcolor") && sender instanceof Player) {
            if (args.length == 1) {
                List<String> permittedColors = userDataManager.getPlayerColors(sender.getName());
                List<String> completions = new ArrayList<>();
                ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
                for (String key : permittedColors) {
                    completions.add(colors.getString(key + ".description"));
                }
                completions.add("clear");
                return completions;
            }
        }

        if (command.getName().equalsIgnoreCase("pandynickcolor")) {
            if (args.length == 1) {
                return Collections.singletonList("reload");
            }
        }
        return new ArrayList<>();
    }

    private String getMessage(String key) {
        String message = plugin.getMessages().getString(key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}