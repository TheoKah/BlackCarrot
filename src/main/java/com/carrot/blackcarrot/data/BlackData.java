package com.carrot.blackcarrot.data;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import com.carrot.blackcarrot.BlackCarrot;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;


public class BlackData {
	private static File dataFile;
	private static ConfigurationNode itemsNode;
	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static Map<String, Item> items = new HashMap<>();

	public static enum Type {
		POSSESS,
		USE
	}

	public static final ImmutableList<String> ContenerMatches = 
			ImmutableList.of(
					"ItemType",
					"UnsafeDamage",
					"UnsafeData.ench",
					"UnsafeData.StoredEnchantments",
					"UnsafeData.Potion",
					"UnsafeData.EntityTag");

	public static void init(File rootDir) throws IOException
	{
		dataFile = new File(rootDir, "items.json");
		rootDir.mkdirs();
		dataFile.createNewFile();

		loader = HoconConfigurationLoader.builder().setFile(dataFile).build();
		itemsNode = loader.load();
	}

	public static void load() {
		boolean hasErrors = false;

		for (ConfigurationNode itemNode : itemsNode.getNode("items").getChildrenList()) {
			try {
				Item item = itemNode.getValue(TypeToken.of(Item.class));
				items.put(item.getAlias(), item);
			} catch (Exception e) {
				hasErrors = true;
			}
		}
		if (hasErrors)
			BlackCarrot.getLogger().warn("Errors occured while loading BlackCarrot.");
	}

	public static void unload() {
		save();
		items.clear();
	}

	public static void save() {
		boolean hasErrors = false;
		itemsNode.removeChild("items");
		for (Entry<String, Item> entry : items.entrySet()) {
			ConfigurationNode itemNode = itemsNode.getNode("items").getAppendedNode();
			try {
				itemNode.setValue(TypeToken.of(Item.class), entry.getValue());
			} catch (ObjectMappingException e) {
				e.printStackTrace();
				hasErrors = true;
			}
		}
		try {
			loader.save(itemsNode);
		} catch (Exception e) {
			e.printStackTrace();
			hasErrors = true;
		}
		if (hasErrors)
			BlackCarrot.getLogger().error("Unable to save all BlackCarot banned items");
	}

	public static void addItem(Item item) {
		items.put(item.getAlias(), item);
		save();
	}	

	public static void delItem(Item item) {
		items.remove(item.getAlias());
		save();
	}

	public static Optional<Item> getItem(String alias) {
		if (items.containsKey(alias))
			return Optional.of(items.get(alias));
		return Optional.empty();
	}

	public static Collection<Item> getItems() {
		return items.values();
	}

	public static Optional<Item> match(ItemStackSnapshot candidate, UUID world) {
		for (Item item : items.values()) {
			if (item.match(candidate, world))
				return Optional.of(item);
		}
		return Optional.empty();
	}
	
	public static Optional<Item> match(ItemStack candidate, UUID world) {
		for (Item item : items.values()) {
			if (item.match(candidate, world))
				return Optional.of(item);
		}
		return Optional.empty();
	}
	
	public static Optional<Item> match(ItemStackSnapshot candidate, UUID world, Type action) {
		for (Item item : items.values()) {
			if (item.match(candidate, world, action))
				return Optional.of(item);
		}
		return Optional.empty();
	}
	
	public static Optional<Item> match(ItemStack candidate, UUID world, Type action) {
		for (Item item : items.values()) {
			if (item.match(candidate, world, action))
				return Optional.of(item);
		}
		return Optional.empty();
	}
}
