package me.mastergabemod.witherhorse;

import net.minecraft.server.v1_7_R4.EntityHorse;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
    if (!(sender instanceof Player)) {
      sender.sendMessage("");
      return true;
    }

    Player player = (Player)sender;
    Horse.Color color = Horse.Color.WHITE;
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
      }
    }

    if (player.isInsideVehicle()) {
      Entity vehicle = player.getVehicle();
      player.leaveVehicle();
      vehicle.remove();
      player.sendMessage("§b§lYou dismounted your horse.");
    } else if (player.hasPermission("horse.use")) {
      Location loc = player.getLocation();
      net.minecraft.server.v1_7_R4.WorldServer ws = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld)loc.getWorld()).getHandle();
      EntityHorse eh = new net.minecraft.server.v1_7_R4.EntityHorse(ws);
      eh.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
      net.minecraft.server.v1_7_R4.NBTTagCompound compound = new net.minecraft.server.v1_7_R4.NBTTagCompound();
      eh.b(compound);
      compound.setBoolean("Saddle", true);
      eh.a(compound);
      ws.addEntity(eh, CreatureSpawnEvent.SpawnReason.CUSTOM);
      Horse horse = (Horse)eh.getBukkitEntity();
      horse.setTamed(true);
      horse.setOwner(player);
      horse.setColor(color);
      horse.setPassenger(player);
      horse.setMetadata("witherhorse", new FixedMetadataValue(this.plugin, Boolean.valueOf(true)));
      player.sendMessage("§b§lYou have mounted a horse!");
    } else {
      player.sendMessage("§4You don't have permissions to run this command!");
    }
    return true;
  }
}
