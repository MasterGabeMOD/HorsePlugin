package me.mastergabemod.witherhorse;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Objects;

public class HorseListener
  implements Listener
{
  @EventHandler(priority=EventPriority.MONITOR)
  public void onCreatureSpawn(CreatureSpawnEvent event)
  {
    CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
    if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM)
      event.setCancelled(false);
  }

  @EventHandler(priority=EventPriority.MONITOR)
  public void onEntityDismount(EntityDismountEvent event)
  {
    Entity horse = event.getDismounted();
    if (horse.hasMetadata("horse")) {
      horse.remove();
      }
    }
  
  

  @EventHandler(priority=EventPriority.LOWEST)
  public void onEntityDamage(EntityDamageEvent event) {
    Entity entity = event.getEntity();
    if (entity.hasMetadata("horse"))
      event.setCancelled(true);
  }

  @EventHandler(priority=EventPriority.LOWEST)
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    Player player = event.getPlayer();
    Location from = event.getFrom();
    Location to = event.getTo();
    if ((player.isInsideVehicle())) {
      assert to != null;
      if ((!Objects.equals(from.getWorld(), to.getWorld())) || (from.distance(to) > 4.0D)) {
        Entity vehicle = player.getVehicle();
        assert vehicle != null;
        if (vehicle.hasMetadata("horse")) {
          event.setCancelled(true);
          player.leaveVehicle();
          player.teleport(to);
          vehicle.teleport(to);
          vehicle.setPassenger(player);
        }
      }
    }
  }
}