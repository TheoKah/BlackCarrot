package com.carrot.blackcarrot.cmd;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.carrot.blackcarrot.data.BlackLang;
import com.carrot.blackcarrot.data.Item;

public class BlackWorldExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<Item> item = args.<Item>getOne("item");
		
		Optional<WorldProperties> world = args.<WorldProperties>getOne("world");
		if (!item.isPresent() || !world.isPresent()) {
			src.sendMessage(Text.of(TextColors.YELLOW, BlackLang.USAGE_WORLD));
			return CommandResult.success();
		}
		
		item.get().switchWorld(world.get().getUniqueId());
		
		item.get().printInfo(src);
		
		return CommandResult.success();
	}

}
