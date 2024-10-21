package eu.epicdark.epicmagics.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ImageRenderer {
	
	private final BufferedImage image;
	private Location center;
	private float width;
	private float height;
	
	private Particle particleType;
	
	private float sizeX;
	private float sizeY;
	private float sizeZ;
	
	
	private boolean far;
	private float speed;
	private int amount;
	
	
	/**
    *
    * @param file The file of the image.
    * @param location The center location where the image is rendered
    * @throws IOException when the image could not be found
    * @throws IllegalArgumentException when the image is too large (64*64 max)
    */
	public ImageRenderer(File file, Location location, Particle type, int amount, float speed, boolean far) throws IOException {
		
		this.image = ImageIO.read(file);
		this.center = location;
		
		width = image.getWidth();
        height = image.getHeight();

        if (width * height > 64 * 64) {
            throw new IllegalArgumentException("Image dimensions too big!");
        }
	}
	
	public void render() {
		//The scaling factor determines how dense the particles should be together (the higher the denominator, the less the space between the particles/pixels)
        float scalingFactor = 1f / 2.5f;
        
        for(int x = 0; x < image.getWidth(); x++) {
        	for(int y = 0; y < image.getHeight(); y++) {
        		
        		float px = (float) (center.getX() + (x * scalingFactor) - ((width * scalingFactor) / 2));
                float py = (float) (center.getY() + 0);
                float pz = (float) (center.getZ() + (y * scalingFactor) - ((height * scalingFactor) / 2));
                
        	}
        }
        
		this.center.getWorld().spawnParticle(particleType, px, py, pz, amount, sizeX, sizeY, sizeZ, 0, null, far);
	}

}
