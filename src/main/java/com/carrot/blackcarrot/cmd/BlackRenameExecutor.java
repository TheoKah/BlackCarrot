package com.carrot.blackcarrot.cmd;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackData;
import com.carrot.blackcarrot.data.BlackLang;
import com.carrot.blackcarrot.data.Item;

public class BlackRenameExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Item> item = args.<Item>getOne("item");
		Optional<String> alias = args.<String>getOne("alias");
		
		if (!item.isPresent() || !alias.isPresent()) {
			src.sendMessage(Text.of(TextColors.YELLOW, BlackLang.USAGE_RENAME));
			return CommandResult.success();
		}

		if (BlackData.getItem(alias.get()).isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_ALIASEXISTS));
			return CommandResult.success();
		}
		BlackData.delItem(item.get());
		item.get().setAlias(alias.get());
		BlackData.addItem(item.get());
		
		item.get().printInfo(src);
		
		return CommandResult.success();
	}

}
