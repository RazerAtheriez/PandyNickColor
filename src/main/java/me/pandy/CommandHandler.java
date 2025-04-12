package me.pandy;

import me.pandy.api.PandyNickColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter, Listener {
    private final PandyNickColor plugin;
    private final UserDataManager userDataManager;
    private final TabManager tabManager;
    private final PandyNickColorAPI api;

    public CommandHandler(PandyNickColor plugin, UserDataManager userDataManager, TabManager tabManager) {
        this.plugin = plugin;
        this.userDataManager = userDataManager;
        this.tabManager = tabManager;
        this.api = new PandyNickColorAPI(plugin, userDataManager, tabManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pandynickcolor")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage("§6[PandyNickColor] §eКоманды:");
                sender.sendMessage("§e/pandynickcolor reload §7- Перезагрузить конфигурацию");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("pandynickcolor.reload")) {
                    sender.sendMessage(plugin.getMessages().getString("no-permission"));
                    return true;
                }
                plugin.reloadConfigs();
                sender.sendMessage("§6[PandyNickColor] §eКонфигурация перезагружена!");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("nickcolor")) {
            if (args.length < 2) {
                sender.sendMessage("§6[PandyNickColor] §eИспользование: /nickcolor <цвет> <игрок> или /nickcolor clear <игрок>");
                return true;
            }

            String colorKey = args[0];
            String targetPlayerName = args[1];

            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
            if (targetPlayer == null || !targetPlayer.hasPlayedBefore()) {
                sender.sendMessage("§cИгрок " + targetPlayerName + " не найден!");
                return true;
            }

            if (colorKey.equalsIgnoreCase("clear")) {
                api.resetNickColor(targetPlayer);
                userDataManager.setPlayerColors(targetPlayer.getName(), new ArrayList<>());
                userDataManager.saveUserData();
                sender.sendMessage("§6[PandyNickColor] §eЦвет ника игрока " + targetPlayer.getName() + " очищен!");
                return true;
            }

            ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
            if (colors == null || !colors.contains(colorKey)) {
                sender.sendMessage("§cЦвет " + colorKey + " не найден в конфигурации!");
                return true;
            }

            if (!sender.hasPermission("pandynickcolor.color." + colorKey)) {
                sender.sendMessage(plugin.getMessages().getString("no-permission"));
                return true;
            }

            api.setNickColor(targetPlayer, colorKey);
            sender.sendMessage("§6[PandyNickColor] §eЦвет ника игрока " + targetPlayer.getName() + " установлен на " + colorKey + "!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("tabcolor")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cЭта команда только для игроков!");
                return true;
            }

            Player player = (Player) sender;
            if (args.length == 0) {
                List<String> availableColors = api.getAvailableColors(player);
                if (availableColors.isEmpty()) {
                    player.sendMessage("§cУ вас нет доступных цветов!");
                    return true;
                }

                player.sendMessage("§6[PandyNickColor] §eВаши доступные цвета:");
                for (String color : availableColors) {
                    String description = api.getColorDescription(color);
                    player.sendMessage("§e- " + color + (description != null ? " (" + description + ")" : ""));
                }
                return true;
            }

            String colorKey = args[0];
            if (colorKey.equalsIgnoreCase("reset")) {
                api.resetNickColor(player);
                player.sendMessage("§6[PandyNickColor] §eЦвет вашего ника очищен!");
                return true;
            }

            if (!api.hasNickColor(player, colorKey)) {
                player.sendMessage("§cУ вас нет доступа к цвету " + colorKey + "!");
                return true;
            }

            api.setNickColor(player, colorKey);
            player.sendMessage("§6[PandyNickColor] §eВаш цвет ника изменён на " + colorKey + "!");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("pandynickcolor")) {
            if (args.length == 1) {
                completions.add("reload");
                completions.add("help");
            }
        }

        if (command.getName().equalsIgnoreCase("nickcolor")) {
            if (args.length == 1) {
                ConfigurationSection colors = plugin.getConfig().getConfigurationSection("colors");
                if (colors != null) {
                    completions.addAll(colors.getKeys(false));
                }
                completions.add("clear");
            } else if (args.length == 2) {
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (offlinePlayer.getName() != null) {
                        completions.add(offlinePlayer.getName());
                    }
                }
            }
        }

        if (command.getName().equalsIgnoreCase("tabcolor")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    completions.addAll(api.getAvailableColors((Player) sender));
                    completions.add("reset");
                }
            }
        }

        return completions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> colors = userDataManager.getPlayerColors(player.getName());
        if (!colors.isEmpty()) {
            tabManager.applyNickColor(player, colors.get(0), plugin);
        }
    }
}