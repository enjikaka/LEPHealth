package se.enji.lep;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Health extends JavaPlugin implements Listener {
	FileConfiguration config;
	String notAllowed, wrongWay;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		notAllowed = config.getString("messages.error.notAllowed");
		wrongWay = config.getString("messages.error.wrongWay");
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntityType().equals(EntityType.PLAYER) && e.getEntity().hasMetadata("godMode")) e.setCancelled(e.getEntity().getMetadata("godMode").get(0).asBoolean());
	}
	
	private boolean allow(Player p, String a) {
		return p.hasPermission("lep.health.*") || p.hasPermission("lep.health." + a);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("heal")) {
			if (!(sender instanceof Player)) return false;
			Player p = (Player)sender;
			if (args.length != 0 || !allow(p,"heal")) {
				if (args.length != 0) p.sendMessage(config.getString("messages.error.wrongWay"));
				if (!allow(p,"heal")) p.sendMessage(config.getString("messages.error.notAllowed"));
				return false;
			}
			p.setHealth(20);
			p.setFoodLevel(20);
			p.sendMessage(config.getString("messages.heal"));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("godmode")) {
			if (!(sender instanceof Player)) return false;
			Player p = (Player)sender;
			if (args.length != 0 || !allow(p,"godmode")) {
				if (args.length != 0) p.sendMessage(config.getString("messages.error.wrongWay"));
				if (!allow(p,"godmode")) p.sendMessage(config.getString("messages.error.notAllowed"));
				return false;
			}
			if (p.hasMetadata("godMode") && p.getMetadata("godMode").get(0).asBoolean() == true) {
				p.setMetadata("godMode", new FixedMetadataValue(this,false));
				p.sendMessage(config.getString("messages.godmode.disabled"));
				return true;
			}
			p.setMetadata("godMode", new FixedMetadataValue(this,true));
			p.sendMessage(config.getString("messages.godmode.enabled"));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("killp")) {
			if (args.length == 1) {
				Bukkit.getPlayer(args[0]).setHealth(0);
			}
		}
		return false; 
	}
}