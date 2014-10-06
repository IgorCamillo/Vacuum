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
	
	private Map<UUID, Integer> getUsing = new HashMap<UUID, Integer>();
	private ArrayList<UUID> cooldown = new ArrayList<UUID>();
	private ArrayList<UUID> vector = new ArrayList<UUID>();
	
	private int distance = 10;
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction().name().contains("RIGHT")) {
			if (p.hasPermission("vacuum.use") || p.isOp()) {
				if (p.getItemInHand().getType() == Material.ENDER_PEARL) {
					e.setCancelled(true);
					p.updateInventory();

					if (cooldown.contains(p.getUniqueId()))
						return;

					for (Entity entity : p.getNearbyEntities(distance, distance, distance)) {
						Entity outroEntity = entity;
						Location de = outroEntity.getLocation();
						Location para = p.getLocation();

						de.setY(de.getY() + 0.5D);
						
						Vector v = p.getVelocity();
						
						//Depois irei calcular os vetores estou com preguiça :/
						
						if ((outroEntity instanceof Player)) {
							outroEntity.setVelocity(v);
						} else {
							outroEntity.setVelocity(v);
						}

						if (!getUsing.containsKey(p.getUniqueId()) {
							getUsing.put(p.getUniqueId(), 1);
						 } else {
							getUsing.put(p.getUniqueId(), (getUsing.get(p.getUniqueId())).intValue() + 1);
						}
						  if ((getUsing.containsKey(p.getUniqueId())) && (getUsing.get(p.getUniqueId()) >= 2) {
							runCooldown.(p);
						}
					}
				}
			}
		}
	}
	
	public void runCooldown(Player p) {
		cooldown.add(p.getUniqueId());
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				cooldown.remove(p.getUniqueId());
			}
		}, 20*12L);
	}
}
