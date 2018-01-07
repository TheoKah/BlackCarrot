package com.carrot.blackcarrot.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import com.carrot.blackcarrot.BlackCarrot;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class BlackLang
{
	public static String HELP_MAIN = "Main BlackCarrot command";
	public static String HELP_HELP = "Print a link to the wiki";
	public static String HELP_EDIT = "Edit an item";
	public static String HELP_LIST = "Print the list of banned items";
	public static String HELP_DEBUG = "Print the list of keys/values of the item in hand";
	public static String HELP_INFO = "Print more info about a banned item";
	public static String HELP_ADD = "Add an item to the list";
	public static String HELP_DEL = "Remove an item from the list";
	public static String HELP_RENAME = "Rename an item";
	public static String HELP_REASON = "Change the reason an item is banned";
	public static String HELP_WORLD = "Add or remove the world from the list";
	public static String HELP_ACTION = "Set the action monotoring on or off";
	public static String HELP_SWAPWORLD = "Change whether the world list act as a whitelist or a blacklist";

	public static String ERROR_CONSOLE = "Need to be a player";
	public static String ERROR_BADITEM = "Item not found";
	public static String ERROR_ALIASEXISTS = "This alias is already taken";
	public static String ERROR_ITEMHAND = "You need to have the item in your hand";
	
	public static String SUCCESS_ADD = "Item added to the list!";
	public static String SUCCESS_DEL = "Item deleted from the list!";

	public static String USAGE_ADD = "/bl add [alias] [reason]";
	public static String USAGE_DEL = "/bl del <alias>";
	public static String USAGE_REASON = "/bl edit reason <alias> <reason>";
	public static String USAGE_RENAME = "/bl edit rename <alias> <new name>";
	public static String USAGE_ACTION = "/bl edit action <alias> <type>";
	public static String USAGE_SWAPWORLD = "/bl edit swapworld <alias>";
	public static String USAGE_WORLD = "/bl edit world <alias> <dim>";
	public static String USAGE_EDIT = "/bl edit <alias>";
	public static String USAGE_INFO = "/bl info <alias>";
	
	public static String ERROR_CONFIGFILE = "Could not load or create config file";
	public static String INFO_CONFIGRELOADED = "Config file has been reloaded";
	
	public static String DEFAULT_REASON = "Ban Hammer has spoken!";
	
	public static String CLICKME = " <- click";
	
	private static File languageFile;
	private static ConfigurationLoader<CommentedConfigurationNode> languageManager;
	private static CommentedConfigurationNode language;

	public static void init(File rootDir)
	{
		languageFile = new File(rootDir, "language.conf");
		languageManager = HoconConfigurationLoader.builder().setPath(languageFile.toPath()).build();
		
		try
		{
			if (!languageFile.exists())
			{
				languageFile.getParentFile().mkdirs();
				languageFile.createNewFile();
				language = languageManager.load();
				languageManager.save(language);
			}
			language = languageManager.load();
		}
		catch (IOException e)
		{
			BlackCarrot.getLogger().error("Could not load or create language file !");
			e.printStackTrace();
		}
		
	}
	
	public static void load()
	{
		Field fields[] = BlackLang.class.getFields();
		for (int i = 0; i < fields.length; ++i) {
			if (fields[i].getType() != String.class)
				continue ;
			if (language.getNode(fields[i].getName()).getString() != null) {
				try {
					fields[i].set(String.class, language.getNode(fields[i].getName()).getString());
				} catch (IllegalArgumentException|IllegalAccessException e) {
					BlackCarrot.getLogger().error("Error whey loading language string " + fields[i].getName());
					e.printStackTrace();
				}
			} else {
				try {
					language.getNode(fields[i].getName()).setValue(fields[i].get(String.class));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					BlackCarrot.getLogger().error("Error whey saving language string " + fields[i].getName());
					e.printStackTrace();
				}
			}
		}
		
		save();
	}

	public static void save()
	{
		try
		{
			languageManager.save(language);
		}
		catch (IOException e)
		{
			BlackCarrot.getLogger().error("Could not save config file !");
		}
	}
}
