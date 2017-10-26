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
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackLang;

public class BlackMainExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> contents = new ArrayList<>();

		contents.add(Text.of(TextColors.GOLD, "/bl help", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_HELP));
		contents.add(Text.of(TextColors.GOLD, "/bl list", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_LIST));
		contents.add(Text.of(TextColors.GOLD, "/bl info <alias>", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_INFO));
		contents.add(Text.of(TextColors.GOLD, "/bl add [alias]", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_ADD));
		contents.add(Text.of(TextColors.GOLD, "/bl delete <alias>", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_DEL));
		contents.add(Text.of(TextColors.GOLD, "/bl rename <alias> <newname>", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_RENAME));
		contents.add(Text.of(TextColors.GOLD, "/bl edit <alias> [action [param]]", TextColors.GRAY, " - ", TextColors.YELLOW, BlackLang.HELP_EDIT));

		PaginationList.builder()
		.title(Text.of(TextColors.GOLD, "{ ", TextColors.YELLOW, "BlackCarrot Help", TextColors.GOLD, " }"))
		.contents(contents)
		.padding(Text.of("-"))
		.sendTo(src);
		return CommandResult.success();
	}

}
