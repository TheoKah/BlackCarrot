package com.carrot.blackcarrot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.carrot.blackcarrot.data.BlackData.Type;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class Item {

	@Setting
	private String alias;

	@Setting
	private String reason = BlackConfig.getNode("config", "defaultReason").getString(BlackLang.DEFAULT_REASON);

	@Setting
	private String id;

	@Setting
	private String name;

	@Setting
	private boolean worldsAsWhitelist = true;

	@Setting
	private Map<String, String> matchContainer = new HashMap<>();

	@Setting
	private Map<BlackData.Type, Boolean> types = new HashMap<>();

	@Setting
	private List<UUID> worlds = new ArrayList<>();

	public Item() {
	}

	public Item(ItemStack item) {
		id = item.getItem().getId();
		name = item.getTranslation().get();
		alias = id;

		for (Entry<DataQuery, Object> type : item.toContainer().getValues(true).entrySet()) {
			if (BlackData.ContenerMatches.contains(type.getKey().toString()))
				matchContainer.put(type.getKey().toString(), type.getValue().toString());
		}

		for (Type action : BlackData.Type.values())
			types.put(action, true);
	}

	public boolean same(ItemStack candidate) {
		if (!id.equals(candidate.getItem().getId()))
			return false;
		
		int matched = 0;
		
		for (Entry<DataQuery, Object> entry : candidate.toContainer().getValues(true).entrySet()) {
			if (matchContainer.containsKey(entry.getKey().toString())) {
				if (!matchContainer.get(entry.getKey().toString()).equals(entry.getValue().toString()))
					return false;
				++matched;
			}
		}
		
		return matched >= matchContainer.size();
	}

	public boolean same(ItemStackSnapshot candidate) {
		if (!id.equals(candidate.getType().getId()))
			return false;
		
		int matched = 0;
		
		for (Entry<DataQuery, Object> entry : candidate.toContainer().getValues(true).entrySet()) {
			if (matchContainer.containsKey(entry.getKey().toString())) {
				if (!matchContainer.get(entry.getKey().toString()).equals(entry.getValue().toString()))
					return false;
				++matched;
			}
		}
		
		return matched >= matchContainer.size();
	}

	public boolean match(ItemStack candidate, UUID world) {
		if (checkContext(world))
			return same(candidate);
		return false;
	}

	public boolean match(ItemStackSnapshot candidate, UUID world) {
		if (checkContext(world))
			return same(candidate);
		return false;
	}

	public boolean match(ItemStack candidate, UUID world, Type action) {
		if (checkContext(world, action))
			return same(candidate);
		return false;
	}

	public boolean match(ItemStackSnapshot candidate, UUID world, Type action) {
		if (checkContext(world, action))
			return same(candidate);
		return false;
	}

	private boolean checkContext(UUID world) {
		if (worlds.contains(world))
			return !worldsAsWhitelist;
		return worldsAsWhitelist;
	}

	private boolean checkContext(UUID world, Type action) {
		if (!types.get(action))
			return false;
		return checkContext(world);
	}

	public void printInfo(CommandSource src) {

		boolean canEdit = src.hasPermission("blackcarrot.command.edit");

		List<Text> contents = new ArrayList<>();

		Builder aliasbuilder = Text.builder(alias).color(TextColors.DARK_GREEN);
		Builder reasonbuilder = Text.builder(reason).color(TextColors.DARK_GREEN);

		if (canEdit) {
			aliasbuilder.onClick(TextActions.suggestCommand("/bl edit rename " + alias + " "));
			aliasbuilder.append(Text.of(TextColors.GRAY, BlackLang.CLICKME));
			reasonbuilder.onClick(TextActions.suggestCommand("/bl edit reason " + alias + " "));
			reasonbuilder.append(Text.of(TextColors.GRAY, BlackLang.CLICKME));
		}

		contents.add(Text.of(TextColors.GOLD, "Alias: ", aliasbuilder));
		contents.add(Text.of(TextColors.GOLD, "Id: ", TextColors.DARK_GREEN, id));
		contents.add(Text.of(TextColors.GOLD, "Name: ", TextColors.DARK_GREEN, name));
		contents.add(Text.of(TextColors.GOLD, "Reason: ", reasonbuilder));

		Builder bTypes = Text.builder();

		for (Entry<Type, Boolean> pair : types.entrySet()) {
			Builder bType = Text.builder(" " + pair.getKey().toString())
					.color(pair.getValue() ? TextColors.DARK_RED : TextColors.DARK_GRAY);
			if (canEdit)
				bType.onClick(TextActions.runCommand("/bl edit action " + alias + " " + pair.getKey().name()));
			bTypes.append(bType.build());
		}

		Builder bActions = Text.builder("Actions :")
				.color(TextColors.GOLD)
				.append(bTypes.build());

		if (canEdit)
			bActions.append(Text.of(TextColors.GRAY, BlackLang.CLICKME));

		contents.add(bActions.build());	

		Builder bWorldList = Text.builder(worldsAsWhitelist ? "Allowed in:" : "Blocked in:")
				.color(TextColors.GOLD);
		if (canEdit) {
			bWorldList.onClick(TextActions.runCommand("/bl edit swapworld " + alias))
			.append(Text.of(TextColors.GRAY, BlackLang.CLICKME));
		}
		contents.add(Text.of(TextColors.GOLD, bWorldList.build()));

		Builder bWorlds = Text.builder();

		for (UUID worldUUID : worlds) {
			Optional<World> world = Sponge.getServer().getWorld(worldUUID);
			if (world.isPresent()) {
				Builder bWorld = Text.builder(" " + world.get().getName())
						.color(worldsAsWhitelist ? TextColors.DARK_GREEN : TextColors.DARK_RED);
				if (canEdit)
					bWorld.onClick(TextActions.runCommand("/bl edit world " + alias + " " + worldUUID));
				bWorlds.append(bWorld.build());
			}
		}

		if (canEdit) {
			Builder bWorld = Text.builder(" [add]")
					.color(TextColors.DARK_GRAY)
					.onClick(TextActions.suggestCommand("/bl edit world " + alias + " "))
					.append(Text.of(TextColors.GRAY, BlackLang.CLICKME));
			bWorlds.append(bWorld.build());
		}
		contents.add(Text.of(bWorlds.build()));

		if (canEdit) {
			contents.add(Text.of(TextColors.GOLD, "Match against:"));
			contents.add(Text.of(TextColors.DARK_PURPLE, "Item ID", TextColors.DARK_GRAY, " = ", TextColors.DARK_AQUA, id));	
			for (Entry<String, String> pair : matchContainer.entrySet()) {
				contents.add(Text.of(TextColors.DARK_PURPLE, pair.getKey(), TextColors.DARK_GRAY, " = ", TextColors.DARK_AQUA, pair.getValue()));	
			}

		}
		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "Banned Item: ", TextColors.DARK_GREEN, alias, TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean getPerm(Type type) {
		if (!types.containsKey(type))
			types.put(type, true);
		return types.get(type);
	}

	public void switchPerm(Type type) {
		if (!types.containsKey(type))
			types.put(type, true);
		types.put(type, !types.get(type));
	}

	public void switchWorldList() {
		worldsAsWhitelist = !worldsAsWhitelist;
	}

	public void switchWorld(UUID worldUUID) {
		if (worlds.contains(worldUUID))
			worlds.remove(worldUUID);
		else
			worlds.add(worldUUID);
	}

	public boolean monitorInWorld(UUID worldUUID) {
		if (worlds.contains(worldUUID)) {
			return !worldsAsWhitelist;
		}
		return worldsAsWhitelist;
	}
}
