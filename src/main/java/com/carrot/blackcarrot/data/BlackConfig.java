package com.carrot.blackcarrot.data;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.BlackCarrot;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class BlackConfig
{
	private static File configFile;
	private static ConfigurationLoader<CommentedConfigurationNode> configManager;
	private static CommentedConfigurationNode config;

	public static void init(File rootDir)
	{
		configFile = new File(rootDir, "config.conf");
		configManager = HoconConfigurationLoader.builder().setPath(configFile.toPath()).build();
	}
	
	public static void load()
	{
		load(null);
	}
	
	public static void load(CommandSource src)
	{
		// load file
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
				config = configManager.load();
				configManager.save(config);
			}
			config = configManager.load();
		}
		catch (IOException e)
		{
			BlackCarrot.getLogger().error(BlackLang.ERROR_CONFIGFILE);
			e.printStackTrace();
			if (src != null)
			{
				src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_CONFIGFILE));
			}
		}
		
		// check integrity
		//Utils.ensurePositiveNumber(config.getNode("prices", "nationCreationPrice"), 2500);
		//Utils.ensureBoolean(config.getNode("others", "enableNationRanks"), true);
		Utils.ensureString(config.getNode("config", "replaceItemBy"), "minecraft:air");
		Utils.ensureString(config.getNode("config", "defaultReason"), BlackLang.DEFAULT_REASON);

		save();
		if (src != null)
		{
			src.sendMessage(Text.of(TextColors.GREEN, BlackLang.INFO_CONFIGRELOADED));
		}
	}

	public static void save()
	{
		try
		{
			configManager.save(config);
		}
		catch (IOException e)
		{
			BlackCarrot.getLogger().error("Could not save config file !");
		}
	}

	public static CommentedConfigurationNode getNode(String... path)
	{
		return config.getNode((Object[]) path);
	}
	
	public static class Utils
	{
		public static void ensureString(CommentedConfigurationNode node, String def)
		{
			if (node.getString() == null)
			{
				node.setValue(def);
			}
		}

		public static void ensurePositiveNumber(CommentedConfigurationNode node, Number def)
		{
			if (!(node.getValue() instanceof Number) || node.getDouble(-1) < 0)
			{
				node.setValue(def);
			}
		}
		
		public static void ensureBoolean(CommentedConfigurationNode node, boolean def)
		{
			if (!(node.getValue() instanceof Boolean))
			{
				node.setValue(def);
			}
		}
	}
}
