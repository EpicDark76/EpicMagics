package eu.epicdark.epicmagics.spells;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import eu.epicdark.epicmagics.EpicMagics;

public abstract class AnimatedRangedMagicSpell extends RangedMagicSpell {
	private static final Random random = new Random();

	private final Particle particle;

	protected AnimatedRangedMagicSpell(Material material, int cost, int cooldown, int range, Particle particle) {
		super(material, cost, cooldown, range);
		this.particle = particle;
	}

	@Override
	protected boolean handle(Player player, Location target) {
		World world = player.getWorld();
		Location location = player.getEyeLocation();
		
		double distance = location.distance(target);
		int steps = (int) (distance * 0.75);
		double curveMagnitude = distance * 0.3;
		Location controlPoint = getRandomControlPoint(location, target, curveMagnitude);

		// Add a task to create a particle trail moving over time
		new BukkitRunnable() {
			int currentStep = 0;

			@Override
			public void run() {
				if (currentStep >= steps) {
					// Cancel the task when all steps are completed
					this.cancel();
					// do action at target location when particle curve hits
					handleImpact(target);
					return;
				}

				// Calculate t value for interpolation (from 0 to 1 as the steps progress)
				double t = (double) currentStep / steps;

				// Interpolate between player and target positions with the consistent control
				// point for the curve
				Location interpolatedLocation = getCurvedLocation(location, target, controlPoint, t);

				// Spawn particle at the interpolated location
				world.spawnParticle(particle, interpolatedLocation, 0, 0, 0, 0, 0.1, null, true);

				// Increment the step
				currentStep++;
			}
		}.runTaskTimer(EpicMagics.INSTANCE, 0L, 1L); // Runs every tick (20 ticks = 1 second)
		return true;
	}

	protected abstract boolean handleImpact(Location target);

	// Method to get a consistent control point for the entire curve with a random
	// direction
	private Location getRandomControlPoint(Location start, Location end, double curveMagnitude) {
		// Generate random direction for the control point
		Vector randomDirection = new Vector(
				(random.nextDouble() - 0.5) * 2, // Random X direction
				(random.nextDouble() - 0.5) * 2, // Random Y direction
				(random.nextDouble() - 0.5) * 2 // Random Z direction
		).normalize(); // Normalize to make it a unit vector

		// Scale the random direction by the curve magnitude to control how much the
		// curve bends
		randomDirection.multiply(curveMagnitude);

		// Set the control point as the midpoint between start and end, offset by the
		// random direction
		return start.clone().add(end).multiply(0.5).add(randomDirection);
	}

	// Method to get a smoothly curved location between two points using Bézier
	// curve interpolation
	private Location getCurvedLocation(Location start, Location end, Location controlPoint, double t) {
		double oneMinusT = 1 - t;

		// Quadratic Bézier formula: B(t) = (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
		double x = oneMinusT * oneMinusT * start.getX() + 2 * oneMinusT * t * controlPoint.getX() + t * t * end.getX();
		double y = oneMinusT * oneMinusT * start.getY() + 2 * oneMinusT * t * controlPoint.getY() + t * t * end.getY();
		double z = oneMinusT * oneMinusT * start.getZ() + 2 * oneMinusT * t * controlPoint.getZ() + t * t * end.getZ();

		return new Location(start.getWorld(), x, y, z);
	}
}
