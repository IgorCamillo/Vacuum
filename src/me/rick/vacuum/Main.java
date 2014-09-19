package me.rick.vacuum;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener {
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	
	ArrayList<UUID> cooldown = new ArrayList<UUID>();
	ArrayList<UUID> vector = new ArrayList<UUID>();
	
	int distance = 10;
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
		if (p.hasPermission("vacuum.use")) {
		if (p.getItemInHand().getType() == Material.ENDER_PEARL) {
			e.setCancelled(true);
			p.updateInventory();
			
			if (cooldown.contains(p.getUniqueId())) return;
			
			ArrayList<Entity> entityList = new ArrayList<Entity>();
			
			for (int i = 0; i < distance+1; i++) {
			for (Entity entity : p.getNearbyEntities(i, i*2, i)) {
				entityList.add(entity);
			}
			if (!entityList.isEmpty()) {
				break;
			}
			}
			
			if (!entityList.isEmpty()) {
			
			Entity entityTarget = null;
			
			if (entityList.get(0) != null) {
				entityTarget = entityList.get(0);
			}
			
			if (entityTarget != null) {
			
			int distanceY = 0;
			
			if (p.getLocation().getY() > entityTarget.getLocation().getY()) {
				distanceY = (int)p.getLocation().getY()-(int)entityTarget.getLocation().getY();
			}else{
				distanceY = (int)entityTarget.getLocation().getY()-(int)p.getLocation().getY();
			}
			
			int distance = (int) p.getLocation().distance(entityTarget.getLocation());
			
			distance = distance-distanceY;
			
			if (entityTarget instanceof Item) {
				entityTarget.setVelocity(p.getLocation().toVector().subtract(entityTarget.getLocation().toVector()).normalize());
				runCooldown(p);
			}else{
				if (!vector.contains(entityTarget.getUniqueId())) {
				if ((distance < 2) && (distanceY >= 3)) {
					entityTarget.setVelocity(new Vector(0, 2, 0));
					vector.add(entityTarget.getUniqueId());
					runCooldown(p);
					return;
				}
				if ((distanceY == 0) && (distance < 2)) {
					entityTarget.teleport(p.getWorld().getHighestBlockAt(p.getLocation()).getLocation());
					runCooldown(p);
					return;
				}
				entityTarget.setVelocity(p.getLocation().toVector().subtract(entityTarget.getLocation().toVector()).setY(distanceY).normalize());
				runCooldown(p);
				}
			}
			}
			}
		}
		}
		}
	}
	
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(vector.contains(e.getEntity().getUniqueId()) && (e.getCause() == DamageCause.FALL)) {
			vector.remove(e.getEntity().getUniqueId());
		}
	}
	
	public void runCooldown(Player p) {
		cooldown.add(p.getUniqueId());
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				cooldown.remove(p.getUniqueId());
			}
		}, 10L);
	}
}