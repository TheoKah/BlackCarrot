package com.carrot.blackcarrot.cmd;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackLang;
import com.carrot.blackcarrot.data.Item;

public class BlackSwapWorldExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Item> item = args.<Item>getOne("item");
		
		if (!item.isPresent()) {
			src.sendMessage(Text.of(TextColors.YELLOW, BlackLang.USAGE_SWAPWORLD));
			return CommandResult.success();
		}
		
		item.get().switchWorldList();
		
		item.get().printInfo(src);
		
		return CommandResult.success();
	}

}
