package com.carrot.blackcarrot.cmd;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackData;
import com.carrot.blackcarrot.data.Item;

public class BlackListExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> contents = new ArrayList<>();

		for (Item item : BlackData.getItems()) {
			contents.add(Text.builder(item.getAlias())
					.color(TextColors.GOLD)
					.onClick(TextActions.runCommand("/bl i " + item.getAlias())).build());
		}
		
		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "Black List", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}

}
