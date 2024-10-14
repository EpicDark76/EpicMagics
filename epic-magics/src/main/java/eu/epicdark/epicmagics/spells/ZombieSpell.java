package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class ZombieSpell extends MobSpell{

	public ZombieSpell() {
		super(Material.ROTTEN_FLESH, 32, seconds(10), 50, Particle.FIREWORK);
	}

	@Override
	public LivingEntity createEntity(Location location) {
		Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		zombie.setAdult();
		zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
		zombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		zombie.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
		zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		return zombie;
	}

}
