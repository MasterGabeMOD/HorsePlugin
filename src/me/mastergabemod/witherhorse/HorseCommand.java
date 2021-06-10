package me.mastergabemod.witherhorse;

import net.minecraft.server.v1_16_R3.EntityHorse;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class HorseCommand
  implements CommandExecutor
{
  private HorsePlugin plugin;

  public HorseCommand(HorsePlugin plugin)
  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (!(sender instanceof Player player)) {
      sender.sendMessage("");
      return true;
    }

    Horse.Color color = Horse.Color.WHITE;
    if (args.length > 0) {
      try {
        color = Horse.Color.valueOf(args[0].toUpperCase());
      } catch (Exception e) {
        StringBuilder builder = new StringBuilder();
        for (Horse.Color c : Horse.Color.values()) {
          builder.append(", ").append(c.toString().toLowerCase());
        }

        player.sendMessage("§4§lInvalid color! Valid colors are: §e"+builder.substring(2));
        return false;
      }
    }

    if (player.isInsideVehicle()) {
      Entity vehicle = player.getVehicle();
      player.leaveVehicle();
      assert vehicle != null;
      vehicle.remove();
    } else if (player.hasPermission("horse.use")) {
      Location loc = player.getLocation();

      WorldServer ws = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();
      EntityHorse h = new EntityHorse(EntityTypes.HORSE, ws);
      h.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      ws.addEntity(h, CreatureSpawnEvent.SpawnReason.CUSTOM);

      Horse horse = (Horse) h.getBukkitEntity();
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      horse.setTamed(true);
      horse.setOwner(player);
      horse.setColor(color);
      horse.setPassenger(player);
      horse.setMetadata("witherhorse", new FixedMetadataValue(this.plugin, Boolean.TRUE));
      player.sendMessage("§b§lYou have mounted a horse!");
    } else {
      player.sendMessage("§4You don't have permissions to run this command!");
    }
    return true;
  }
}
