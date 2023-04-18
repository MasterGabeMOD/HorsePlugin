package me.mastergabemod.witherhorse;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class HorseCommand implements CommandExecutor, Listener {
    private final HorsePlugin plugin;

    public HorseCommand(HorsePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("");
            return true;
        }

        Player player = (Player) sender;
        final Horse.Color color;
        if (args.length > 0) {
            try {
                color = Horse.Color.valueOf(args[0].toUpperCase());
            } catch (Exception e) {
                player.sendMessage("§4Invalid color, valid colors:");
                StringBuilder builder = new StringBuilder();
                for (Horse.Color c : Horse.Color.values()) {
                    builder.append(" ,").append(c.toString().toLowerCase());
                }

                player.sendMessage(builder.toString().substring(2));
                return true;
            }
        } else {
            color = Horse.Color.values()[(int) (Math.random() * Horse.Color.values().length)];
        }

        if (player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse) {
            Entity vehicle = player.getVehicle();
            player.leaveVehicle();
            vehicle.remove();
            player.sendMessage("§b§lYou dismounted your horse.");
        } else if (player.hasPermission("horse.use")) {
            Location loc = player.getLocation();
            loc.setY(loc.getY() + 1);
            AbstractHorse horse = (AbstractHorse) loc.getWorld().spawn(loc, Horse.class, spawnedHorse -> {
                spawnedHorse.setColor(color);
                spawnedHorse.setTamed(true);
                spawnedHorse.setOwner(player);
                spawnedHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                spawnedHorse.setMetadata("horse", new FixedMetadataValue(plugin, true));
                spawnedHorse.setJumpStrength(Math.random() * 1.5 + 0.5);
                spawnedHorse.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED)
                  .setBaseValue(Math.random() * 0.2 + 0.2);
            });
            player.sendMessage("§b§lYou have mounted a horse!");
            horse.addPassenger(player);
        } else {
            player.sendMessage("§4You don't have permissions to run this command!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerDismount(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getExited();
        Entity vehicle = event.getVehicle();
        if (vehicle instanceof AbstractHorse && vehicle.hasMetadata("horse") && vehicle.getPassengers().isEmpty()) {
            vehicle.remove();
            player.sendMessage("§b§lYou dismounted your horse.");
        }
    }
}
