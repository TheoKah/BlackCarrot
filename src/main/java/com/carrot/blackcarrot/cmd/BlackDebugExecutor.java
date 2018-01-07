package com.carrot.blackcarrot.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackLang;

public class BlackDebugExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_CONSOLE));
			return CommandResult.success();
		}
		
		Player player = (Player) src;
		Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
		
		if (!item.isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_ITEMHAND));
			return CommandResult.success();
		}
		
		List<Text> contents = new ArrayList<>();

		for (Entry<DataQuery, Object> type : item.get().toContainer().getValues(true).entrySet()) {
			contents.add(Text.of(TextColors.DARK_GREEN, type.getKey().toString(), TextColors.DARK_GRAY, " = ", TextColors.YELLOW, type.getValue().toString()));
		}

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "Debug Item", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}

}
