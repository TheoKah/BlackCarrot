package com.carrot.blackcarrot.cmd;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.carrot.blackcarrot.data.BlackData;
import com.carrot.blackcarrot.data.BlackLang;
import com.carrot.blackcarrot.data.Item;

public class BlackAddExecutor implements CommandExecutor{

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_CONSOLE));
			return CommandResult.success();
		}

		Optional<String> alias = args.<String>getOne("alias");
		Optional<String> reason = args.<String>getOne("reason");

		Player player = (Player) src;

		Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
		if (!item.isPresent()) {
			item = player.getItemInHand(HandTypes.OFF_HAND);
			if (!item.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_ITEMHAND));
				return CommandResult.success();
			}
		}

		if (alias.isPresent()) {
			if (BlackData.getItem(alias.get()).isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_ALIASEXISTS));
				return CommandResult.success();
			}
		} else {
			if (BlackData.getItem(item.get().getItem().getId()).isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, BlackLang.ERROR_ALIASEXISTS));
				return CommandResult.success();
			}
		}

		Item banItem = new Item(item.get());

		if (alias.isPresent())
			banItem.setAlias(alias.get());

		if (reason.isPresent())
			banItem.setReason(reason.get());

		BlackData.addItem(banItem);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, BlackLang.SUCCESS_ADD));
		
		banItem.printInfo(src);

		return CommandResult.success();
	}

}
