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
import com.carrot.blackcarrot.data.BlackData.Type;

public class BlackActionExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Item> item = args.<Item>getOne("item");
		Optional<Type> type = args.<Type>getOne("type");
		
		if (!item.isPresent() || !type.isPresent()) {
			src.sendMessage(Text.of(TextColors.YELLOW, BlackLang.USAGE_ACTION));
			return CommandResult.success();
		}
		item.get().switchPerm(type.get());
		
		item.get().printInfo(src);
		
		return CommandResult.success();
	}

}
