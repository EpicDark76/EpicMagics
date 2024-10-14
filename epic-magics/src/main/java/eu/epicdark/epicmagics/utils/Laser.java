package eu.epicdark.epicmagics.utils;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import eu.epicdark.epicmagics.EpicMagics;

public class Laser {
	private static final float height = 1000f;
	private static final int viewRange = 100; //this is not in blocks, but rather used in a formula
	private static final int interpolationDelay = 5;
	public static final int interpolationDuration = 20*4;
	public static final int interpolationDurationExplosion = 4;
	
	private final Location target;

	private BlockDisplay inner;
	private BlockDisplay outer;

	private Quaternionf leftRotation = new Quaternionf(0, 0, 0, 1);
	private Quaternionf rightRotation = new Quaternionf(0, 0, 0, 1);
	
	private final float innerWidth = 0.01f;
	private final float innerWidth2 = 2f;
	private final float outerWidth = 0.05f;
	private final float outerWidth2 = 3f;
	
	private Vector3f innerTranslation = new Vector3f(-(innerWidth/2), 0f, -(innerWidth/2));
	private Vector3f innerScale = new Vector3f(innerWidth, height, innerWidth);
	private Vector3f innerTranslation2 = new Vector3f(-(innerWidth2/2), 0f, -(innerWidth2/2));
	private Vector3f innerScale2 = new Vector3f(innerWidth2, height, innerWidth2);
	private Transformation innerTrans = new Transformation(innerTranslation, leftRotation, innerScale, rightRotation);
	private Transformation innerTrans2 = new Transformation(innerTranslation2, leftRotation, innerScale2, rightRotation);
	
	private Vector3f outerTranslation = new Vector3f(-(outerWidth/2), 0f, -(outerWidth/2));
	private Vector3f outerScale = new Vector3f(outerWidth, height, outerWidth);
	private Vector3f outerTranslation2 = new Vector3f(-(outerWidth2/2), 0f, -(outerWidth2/2));
	private Vector3f outerScale2 = new Vector3f(outerWidth2, height, outerWidth2);
	private Transformation outerTrans = new Transformation(outerTranslation, leftRotation, outerScale, rightRotation);
	private Transformation outerTrans2 = new Transformation(outerTranslation2, leftRotation, outerScale2, rightRotation);

	public Laser(Location target) {
		this.target = target;

		this.inner = target.getWorld().spawn(target, BlockDisplay.class, bd -> {
			bd.setBlock(Material.SMOOTH_QUARTZ.createBlockData());
			bd.setBrightness(new Brightness(15, 15));
			bd.setViewRange(viewRange);
			bd.setTransformation(innerTrans);
		});
		
		
		this.outer = target.getWorld().spawn(target, BlockDisplay.class, bd -> {
			bd.setBlock(Material.WHITE_STAINED_GLASS.createBlockData());
			bd.setBrightness(new Brightness(15, 15));
			bd.setViewRange(viewRange);
			bd.setTransformation(outerTrans);
		});
		
		this.target.getWorld().playSound(target, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 10, 0.6f);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				inner.setInterpolationDelay(interpolationDelay);
				inner.setInterpolationDuration(interpolationDuration);
				inner.setTransformation(innerTrans2);
				
				
				outer.setInterpolationDelay(interpolationDelay);
				outer.setInterpolationDuration(interpolationDuration);
				outer.setTransformation(outerTrans2);
				
			}
		}.runTaskLaterAsynchronously(EpicMagics.INSTANCE, 10);
	}
	
	public void remove() {
		this.inner.remove();
		this.outer.remove();
	}

	public void explode(Player player) {
		Location source = target;
		World world = source.getWorld();
		final int power = 10;
		world.playSound(target, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 7, 0.6f);
		
		this.inner.setInterpolationDelay(0);
		this.inner.setInterpolationDuration(interpolationDurationExplosion);
		this.inner.setTransformation(innerTrans);
		
		this.outer.setInterpolationDelay(0);
		this.outer.setInterpolationDuration(interpolationDurationExplosion);
		this.outer.setTransformation(outerTrans);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				world.playSound(source, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 7, (int) 0.6);
				//world.playSound(target, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 7, 0.5f);
				
				world.spawnParticle(Particle.FLASH, source, 40, 5, 5, 5, 0, null, true);
				world.spawnParticle(Particle.EXPLOSION_EMITTER, source, 10, 5, 5, 5, 0, null, true);
				
				//world.createExplosion(source, 20, true, true, player);
				sphereAround(source, power).forEach(block -> block.setType(Material.AIR));
				remove();
			}
		}.runTaskLater(EpicMagics.INSTANCE, interpolationDurationExplosion);
	}

	public static Set<Block> sphereAround(Location location, int radius) {
		Set<Block> sphere = new HashSet<Block>();
		Block center = location.getBlock();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {
					Block b = center.getRelative(x, y, z);
					if (center.getLocation().distance(b.getLocation()) <= radius) {
						sphere.add(b);
					}
				}

			}
		}
		return sphere;
	}

}
