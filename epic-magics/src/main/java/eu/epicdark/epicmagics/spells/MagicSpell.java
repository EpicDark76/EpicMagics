package eu.epicdark.epicmagics.spells;

import java.util.HashSet;
import java.util.function.Function;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public abstract class MagicSpell {
	private static final HashSet<MagicSpell> SPELLS = new HashSet<MagicSpell>();
	
	protected static final SoundCategory castingCategory = SoundCategory.AMBIENT;
	
	public static final MagicSpell LIGHTNING = new LightningSpell(),
									ORBITAL_STRIKE = new OrbitalStrikeSpell(),
									TIME_STOP = new TimeStopSpell(),
									ZOMBIE = new ZombieSpell(),
									SHIELD = new ShieldSpell(),
									CREEPER = new CreeperSpell(),
									FIREBALL = new FireballSpell(),
									WITHER_SKULL = new WitherskullSpell(),
									SKELETON = new SkeletonSpell(),
									SONIC_STRIKE = new SonicStrikeSpell(),
									TNT = new TntSpell();
	
	private final Material material;
	private final int cost;
	private final int cooldown;
	
	protected MagicSpell(Material material, int cost, int cooldown) {
		this.material = material;
		this.cost = cost;
		this.cooldown = cooldown;
		
		if(checkDuplicate(material)) {
			SPELLS.add(this);
		}else {
			throw new RuntimeException("Tried to register magic spell with cost material already used by another spell!");
		}
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public void apply(Player player) {
		if(handle(player)) {
			playCastingSound(player);
			
			if(player.getGameMode() != GameMode.CREATIVE) {
				player.setCooldown(material, cooldown);
				player.getInventory().getItemInOffHand().setAmount(player.getInventory().getItemInOffHand().getAmount()-cost);
			}
		}
	}
	
	protected void playCastingSound(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, castingCategory, 1, 1);
	}
	
	protected static int seconds(int seconds) {
		return seconds*20;
	}
	
	protected static int minutes(int minutes) {
		return minutes*60*20;
	}
	
	protected abstract boolean handle(Player player);
	
	
	
	
	
	
	public static MagicSpell byMaterial(Material material) {
		for(MagicSpell spell : SPELLS) {
			if(spell.getMaterial() == material) {
				return spell;
			}
		}
		
		return null;
	}
	
	private static boolean checkDuplicate(Material material) {
		return byMaterial(material) == null;
	}
	
	
	
	
	
	
	
	public class MagicSpellWrapper extends MagicSpell {

		private Function<Player, Boolean> handle;
		
		protected MagicSpellWrapper(Material material, int cost, int cooldown, Function<Player, Boolean> handle) {
			super(material, cost, cooldown);
		}

		@Override
		public boolean handle(Player player) {
			return handle.apply(player);
		}
		
	}

}
