package eu.epicdark.epicmagics.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class SkeletonSpell extends MobSpell{

	public SkeletonSpell() {
		super(Material.BONE, 16, seconds(60), 80, Particle.FIREWORK);
	}

	@Override
	public LivingEntity createEntity(Location location) {
		Skeleton skeleton = location.getWorld().spawn(location, Skeleton.class);
		skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
		skeleton.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
		skeleton.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		skeleton.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
		skeleton.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
		return skeleton;
	}

}
