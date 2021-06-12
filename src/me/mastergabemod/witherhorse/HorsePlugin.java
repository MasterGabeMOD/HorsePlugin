package me.mastergabemod.witherhorse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HorsePlugin extends JavaPlugin
{
  private final List<String> used = new ArrayList<>();
  public int command_delay;

  public void onEnable()
  {
    PluginManager pm = getServer().getPluginManager();
    FileConfiguration config = getConfig();
    config.addDefault("command-delay", 600);
    config.options().copyDefaults(true);

    this.command_delay = config.getInt("command-delay");

    pm.registerEvents(new HorseListener(), this);
    Objects.requireNonNull(getCommand("horse")).setExecutor(new HorseCommand(this));
  }

  public boolean canMount(Player player) {
    final String name = player.getName();
    if (!this.used.contains(name)) {
      this.used.add(name);
      Bukkit.getScheduler().runTaskLater(this, () -> HorsePlugin.this.used.remove(name)
              , this.command_delay * 20L);

      return true;
    }
    return false;
  }
}