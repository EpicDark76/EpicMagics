package eu.epicdark.epicmagics.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import eu.epicdark.epicmagics.EpicMagics;

public class Laser implements OrbitalLaser{
	private static final float height = 1000f;
	private static final int viewRange = 100; //this is not in blocks, but rather used in a formula
	private static final int interpolationDelay = 5;
	public static final int interpolationDuration = 20*4;
	private static final int interpolationDurationExplosion = 4;
	private static final int soundVolume = 9;
	
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

	public Laser(Location target, Player cause) {
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
		
		this.target.getWorld().playSound(target, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, soundVolume, 0.6f);
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
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				target.getWorld().playSound(target, Sound.ENTITY_WARDEN_SONIC_CHARGE, SoundCategory.MASTER, soundVolume, 0.5f);
			}
		}.runTaskLater(EpicMagics.INSTANCE, 20);
		new BukkitRunnable() {

			@Override
			public void run() {
				explode(cause);
			}
		}.runTaskLater(EpicMagics.INSTANCE, interpolationDuration);
	}
	
	@Override
	public void remove() {
		this.inner.remove();
		this.outer.remove();
	}

	@Override
	public void explode(Player player) {
		Location source = target;
		World world = source.getWorld();
		final int power = 10;
		world.playSound(target, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, soundVolume, 0.6f);
		
		this.inner.setInterpolationDelay(0);
		this.inner.setInterpolationDuration(interpolationDurationExplosion);
		this.inner.setTransformation(innerTrans);
		
		this.outer.setInterpolationDelay(0);
		this.outer.setInterpolationDuration(interpolationDurationExplosion);
		this.outer.setTransformation(outerTrans);
		
		new BukkitRunnable() {
			
			int tickTime = 0;
			float initialScale = power*2f + 0.1f;
			float scale = initialScale;
			
			@Override
			public void run() {
				
				double density = 0.2;  // smaller numbers make the particles denser

				for (double i=0; i < 2 * Math.PI ; i +=density) {
				     double x = Math.cos(i) * scale + (Math.random()-0.5);
				     double y = Math.sin(i) * scale + (Math.random()-0.5);

				     Location loc = source.clone().add(x, 0.5, y);
				     world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0, 0, 0, 0, null, true);
				}
				
				if(tickTime >= interpolationDurationExplosion-1) {
					Location soundSource = source.clone().add(0, 1, 0);
					world.playSound(soundSource, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, soundVolume, (int) 0.6);
					//world.playSound(soundSource, Sound.ITEM_TOTEM_USE, SoundCategory.MASTER, soundVolume, (int) 0.8);
					//world.playSound(target, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 7, 0.5f);
					
					world.spawnParticle(Particle.EXPLOSION_EMITTER, source, 40, 7, 7, 7, 0, null, true);
					
					BlockUtils.surroundingBlocks(source, power, true).forEach(block -> block.setType(Material.AIR));
					world.createExplosion(source, power*0.8f, true, true, player);
					
					remove();
					cancel();
					return;
				}
				
				scale -= (initialScale/interpolationDurationExplosion+1);
				tickTime++;
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0, 1);
	}

	

}
