package com.carrot.blackcarrot.cmd.element;

import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import com.carrot.blackcarrot.data.BlackData;

public class ItemCmdElement extends PatternMatchingCommandElement
{
	public ItemCmdElement(Text key)
	{
		super(key);
	}
	
	@Override
	protected Iterable<String> getChoices(CommandSource src)
	{
		return BlackData.getItems().stream().map(item -> item.getAlias()).collect(Collectors.toList());
	}

	@Override
	protected Object getValue(String choice) throws IllegalArgumentException
	{
		return BlackData.getItem(choice).get();
	}
}
