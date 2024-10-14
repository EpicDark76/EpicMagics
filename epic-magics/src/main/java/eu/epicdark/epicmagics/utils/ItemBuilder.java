package eu.epicdark.epicmagics.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;

public class ItemBuilder {
	
	private ItemStack item;
	private ItemMeta meta;
	
	public ItemBuilder(Material material) {
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
	}
	
	public ItemBuilder amount(int amount) {
		this.item.setAmount(amount);
		return this;
	}
	
	public ItemBuilder itemName(Component name) {
		this.meta.itemName(name);
		return this;
	}
	
	public ItemBuilder enchantmentGlint(boolean glint) {
		this.meta.setEnchantmentGlintOverride(glint);
		return this;
	}
	
	public ItemBuilder unbreakable() {
		this.meta.setUnbreakable(true);
		return this;
	}
	
	public ItemBuilder rarity(ItemRarity rarity) {
		this.meta.setRarity(rarity);
		return this;
	}
	
	public ItemStack build() {
		this.item.setItemMeta(meta);
		return this.item;
	}

}
