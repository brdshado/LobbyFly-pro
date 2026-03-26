package me.crazybanana4life.lobbyfly.commands;

import me.crazybanana4life.lobbyfly.LobbyFly;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Fly implements CommandExecutor, Listener {
    private LobbyFly plugin;
    private NamespacedKey disabledKey;

    public Fly(LobbyFly plugin) {
        this.plugin = plugin;
        this.disabledKey = new NamespacedKey(plugin, "disabled");
    }

    private boolean isAllowedWorld(World world) {
        List<String> worlds = plugin.getConfig().getStringList("Worlds");
        if (worlds.isEmpty()) {
            String single = plugin.getConfig().getString("World");
            if (single != null && !single.isEmpty()) {
                return world.getName().equalsIgnoreCase(single);
            }
        }
        for (String w : worlds) {
            if (world.getName().equalsIgnoreCase(w)) return true;
        }
        return false;
    }

    private void applyFlight(Player player) {
        if (isAllowedWorld(player.getWorld()) || player.hasPermission("lobbyfly.bypass")) {
            if (player.hasPermission("lobbyfly.use")) {
                boolean isDisabled = player.getPersistentDataContainer().getOrDefault(disabledKey, PersistentDataType.BYTE, (byte) 0) == 1;
                if (!isDisabled) {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                } else {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onPlayerJoinWorld(PlayerChangedWorldEvent event) {
        applyFlight(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                applyFlight(player);
            }
        }, 1L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (isAllowedWorld(player.getWorld()) || player.hasPermission("lobbyfly.bypass")) {
                    if (player.hasPermission("lobbyfly.use")) {
                        boolean isDisabled = player.getPersistentDataContainer().getOrDefault(disabledKey, PersistentDataType.BYTE, (byte) 0) == 1;
                        if (!isDisabled) {
                            player.getPersistentDataContainer().set(disabledKey, PersistentDataType.BYTE, (byte) 1);
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            player.sendMessage(ChatColor.AQUA + "Flight " + ChatColor.RED + "disabled" + ChatColor.AQUA + "!");
                        } else {
                            player.getPersistentDataContainer().set(disabledKey, PersistentDataType.BYTE, (byte) 0);
                            player.setAllowFlight(true);
                            player.setFlying(true);
                            player.sendMessage(ChatColor.AQUA + "Flight " + ChatColor.GREEN + "enabled" + ChatColor.AQUA + "!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You don't have " + ChatColor.GOLD + "permission" + ChatColor.RED + " to use that command!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You have to be in the " + ChatColor.GOLD + "lobby" + ChatColor.RED + " to use that command!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect Usage! " + ChatColor.GOLD + "/fly");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be a " + ChatColor.GOLD + "PLAYER" + ChatColor.RED + " to run that command!");
        }
        return true;
    }
}
