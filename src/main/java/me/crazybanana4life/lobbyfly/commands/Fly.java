package me.crazybanana4life.lobbyfly.commands;

import me.crazybanana4life.lobbyfly.LobbyFly;
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
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Fly implements CommandExecutor, Listener {
    private LobbyFly plugin;
    private NamespacedKey key;

    public Fly(LobbyFly plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "flying");
    }

    private boolean isAllowedWorld(World world) {
        List<String> worlds = plugin.getConfig().getStringList("Worlds");
        for (String w : worlds) {
            if (world.getName().equalsIgnoreCase(w)) return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoinWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (isAllowedWorld(player.getWorld()) || player.hasPermission("lobbyfly.bypass")) {
            if (player.hasPermission("lobbyfly.use")) {
                player.setAllowFlight(true);
                if (player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0) == 1) {
                    player.setFlying(true);
                }
            }
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isAllowedWorld(player.getWorld()) || player.hasPermission("lobbyfly.bypass")) {
            if (player.hasPermission("lobbyfly.use")) {
                player.setAllowFlight(true);
                if (player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0) == 1) {
                    player.setFlying(true);
                }
            }
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onToggle(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("lobbyfly.use")) {
            if (event.isFlying()) {
                player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            } else {
                player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 0);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (isAllowedWorld(player.getWorld()) || player.hasPermission("lobbyfly.bypass")) {
                    if (player.hasPermission("lobbyfly.use")) {
                        if (player.getAllowFlight()) {
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 0);
                            player.sendMessage(ChatColor.AQUA + "Flight " + ChatColor.RED + "disabled" + ChatColor.AQUA + "!");
                        } else {
                            player.setAllowFlight(true);
                            player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
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
